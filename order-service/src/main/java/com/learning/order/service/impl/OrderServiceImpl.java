package com.learning.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learning.common.api.result.PageResult;
import com.learning.common.api.result.Result;
import com.learning.common.starter.exception.BusinessException;
import com.learning.common.starter.utils.LockHelper;
import com.learning.order.dto.CancelOrderRequest;
import com.learning.order.dto.CreateOrderRequest;
import com.learning.order.dto.OrderDTO;
import com.learning.order.dto.OrderQueryRequest;
import com.learning.order.entity.OrderDO;
import com.learning.order.event.OrderEvent;
import com.learning.order.feign.ProductFeignClient;
import com.learning.order.feign.UserFeignClient;
import com.learning.order.mapper.OrderMapper;
import com.learning.order.producer.OrderKafkaProducer;
import com.learning.order.producer.OrderRocketMQProducer;
import com.learning.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private OrderKafkaProducer kafkaProducer;

    @Autowired
    private OrderRocketMQProducer rocketMQProducer;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private LockHelper lockHelper;

    @Override
    @Transactional
    public Long createOrder(CreateOrderRequest request, Long userId) throws Exception {
        // 1. 调用商品服务检查商品是否在售
        Result<Boolean> onSaleResult = productFeignClient.isProductOnSale(request.getProductId(), "mock-token");
        if (onSaleResult == null || !onSaleResult.getData()) {
            throw new BusinessException("商品不存在或已下架");
        }

        // 2. 调用商品服务扣减库存
        Result<Void> deductResult = productFeignClient.deductStock(request.getProductId(),
            request.getQuantity(), "mock-token");
        if (deductResult == null || deductResult.getCode() != 200) {
            throw new BusinessException("库存扣减失败，请重试");
        }

        // 3. 调用用户服务扣减余额
        Result<Void> balanceResult = userFeignClient.deductBalance(userId, request.getTotalPrice(), "mock-token");
        if (balanceResult == null || balanceResult.getCode() != 200) {
            // 回滚库存
            productFeignClient.addStock(request.getProductId(), request.getQuantity(), "mock-token");
            throw new BusinessException("余额不足");
        }

        // 4. 创建订单
        OrderDO order = new OrderDO();
        order.setUserId(userId);
        order.setProductId(request.getProductId());
        order.setProductName(request.getProductName());
        order.setQuantity(request.getQuantity());
        order.setUnitPrice(request.getUnitPrice());
        order.setTotalPrice(request.getTotalPrice());
        order.setStatus(0); // 待支付
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(0); // 未支付
        order.setAddress(request.getAddress());
        order.setReceiver(request.getReceiver());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setRemark(request.getRemark());

        orderMapper.insert(order);

        // 5. 发送Kafka事件
        kafkaProducer.sendOrderCreatedEvent(OrderEvent.created(
            order.getOrderId(), userId, request.getProductId(), request.getProductName(),
            request.getQuantity(), request.getUnitPrice(), request.getTotalPrice()
        ));

        // 6. 发送RocketMQ延迟消息（30分钟超时自动取消）
        rocketMQProducer.sendOrderCancelDelayMessage(order.getOrderId(), userId);

        log.info("订单创建成功: orderId={}, userId={}, amount={}",
            order.getOrderId(), userId, request.getTotalPrice());

        return order.getOrderId();
    }

    @Override
    @Transactional
    public Boolean payOrder(Long orderId, String transactionId, Integer paymentMethod) {
        OrderDO order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new BusinessException("订单不存在");
        }

        // 检查订单状态
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态异常");
        }

        // 使用分布式锁防止重复支付
        lockHelper.executeWithLock("order:pay:" + orderId, () -> {
            // 更新订单状态
            order.setStatus(1); // 待发货
            order.setPaymentStatus(1); // 已支付
            order.setTransactionId(transactionId);
            orderMapper.updateById(order);

            // 发送Kafka事件
            kafkaProducer.sendOrderPaidEvent(OrderEvent.paid(orderId, order.getUserId(), transactionId));

            // 发送RocketMQ支付成功通知
            rocketMQProducer.sendOrderPaymentSuccess(orderId, order.getUserId());

            log.info("订单支付成功: orderId={}, transactionId={}", orderId, transactionId);
        });

        return true;
    }

    @Override
    @Transactional
    public Boolean cancelOrder(CancelOrderRequest request) throws Exception {
        Long orderId = request.getOrderId();

        // 1. 获取订单信息
        OrderDO order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new BusinessException("订单不存在");
        }

        // 2. 检查订单状态（只有待支付可以取消）
        if (order.getStatus() != 0) {
            throw new BusinessException("订单状态不允许取消");
        }

        // 3. 使用分布式锁防止重复取消
        lockHelper.executeWithLock("order:cancel:" + orderId, () -> {
            // 更新订单状态
            order.setStatus(4); // 已取消
            orderMapper.updateById(order);

            // 4. 退款：增加用户余额
            userFeignClient.addBalance(order.getUserId(), order.getTotalPrice(), "mock-token");

            // 5. 恢复库存
            productFeignClient.addStock(order.getProductId(), order.getQuantity(), "mock-token");

            // 6. 发送Kafka事件
            kafkaProducer.sendOrderCancelledEvent(OrderEvent.cancelled(
                orderId, order.getUserId(), request.getCancelReason()
            ));

            // 7. 发送RocketMQ取消通知
            Message<OrderEvent> message = MessageBuilder
                .withPayload(new OrderEvent(orderId, order.getUserId(), "CANCELLED", null, null, null, null, null, request.getCancelReason()))
                .build();
            rocketMQTemplate.syncSend("order-cancel-topic", message);

            log.info("订单取消成功: orderId={}, userId={}, refundAmount={}",
                orderId, order.getUserId(), order.getTotalPrice());
        });

        return true;
    }

    @Override
    public OrderDTO getOrderByOrderId(Long orderId) throws Exception {
        OrderDO order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException("订单不存在");
        }
        return convertToOrderDTO(order);
    }

    @Override
    public PageResult<OrderDTO> getOrdersByUserId(OrderQueryRequest request) {
        Page<OrderDO> page = new Page<>(request.getPage(), request.getSize());

        // 查询用户ID
        Long userId = null;
        if (request.getUsername() != null && !request.getUsername().isEmpty()) {
            // 先查询用户名对应的userId
            // TODO: 从用户服务获取userId
            userId = 1L; // 暂时硬编码
        } else {
            userId = request.getUserId();
        }

        OrderQueryRequest queryRequest = new OrderQueryRequest();
        queryRequest.setUserId(userId);
        queryRequest.setStatus(request.getStatus());
        queryRequest.setPage(request.getPage());
        queryRequest.setSize(request.getSize());

        IPage<OrderDO> orderPage = orderMapper.selectPageByCondition(page, queryRequest);

        PageResult<OrderDTO> result = new PageResult<>();
        result.setCurrent(orderPage.getCurrent());
        result.setSize(orderPage.getSize());
        result.setTotal(orderPage.getTotal());
        result.setRecords(orderPage.getRecords().stream()
            .map(this::convertToOrderDTO)
            .collect(Collectors.toList()));

        return result;
    }

    @Override
    @Transactional
    public Boolean updateOrderStatus(Long orderId, Integer status) {
        orderMapper.updateStatus(orderId, status);
        log.info("订单状态更新: orderId={}, status={}", orderId, status);
        return true;
    }

    @Override
    @Transactional
    public Boolean shipOrder(Long orderId, String trackingNumber) {
        OrderDO order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 1) {
            throw new BusinessException("订单状态异常");
        }

        order.setStatus(2); // 待收货
        orderMapper.updateById(order);

        // 发送Kafka事件
        kafkaProducer.sendOrderPaidEvent(OrderEvent.paid(orderId, order.getUserId(), null));

        // 发送RocketMQ发货通知
        rocketMQProducer.sendOrderShipped(orderId, order.getUserId());

        log.info("订单发货成功: orderId={}, trackingNumber={}", orderId, trackingNumber);
        return true;
    }

    @Override
    @Transactional
    public Boolean completeOrder(Long orderId) {
        OrderDO order = orderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new BusinessException("订单不存在");
        }

        if (order.getStatus() != 2) {
            throw new BusinessException("订单状态异常");
        }

        order.setStatus(3); // 已完成
        orderMapper.updateById(order);

        // 增加商品销量
        // TODO: 调用商品服务增加销量
        log.info("订单完成: orderId={}", orderId);

        // 发送Kafka事件
        kafkaProducer.sendOrderCompletedEvent(OrderEvent.completed(orderId, order.getUserId()));

        return true;
    }

    /**
     * 转换为订单DTO
     */
    private OrderDTO convertToOrderDTO(OrderDO order) {
        OrderDTO dto = new OrderDTO();
        BeanUtils.copyProperties(order, dto);

        // 状态转换
        dto.setStatusText(getStatusText(order.getStatus()));
        dto.setPaymentMethodText(getPaymentMethodText(order.getPaymentMethod()));
        dto.setPaymentStatusText(getPaymentStatusText(order.getPaymentStatus()));

        return dto;
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待支付";
            case 1: return "待发货";
            case 2: return "待收货";
            case 3: return "已完成";
            case 4: return "已取消";
            case 5: return "退款中";
            case 6: return "已退款";
            default: return "未知";
        }
    }

    private String getPaymentMethodText(Integer paymentMethod) {
        switch (paymentMethod) {
            case 1: return "支付宝";
            case 2: return "微信支付";
            case 3: return "余额支付";
            default: return "未知";
        }
    }

    private String getPaymentStatusText(Integer paymentStatus) {
        return paymentStatus == 1 ? "已支付" : "未支付";
    }
}
