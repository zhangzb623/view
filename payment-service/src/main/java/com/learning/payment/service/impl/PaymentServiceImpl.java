package com.learning.payment.service.impl;

import com.learning.common.api.result.Result;
import com.learning.common.starter.exception.BusinessException;
import com.learning.common.starter.utils.CacheHelper;
import com.learning.payment.dto.CreatePaymentRequest;
import com.learning.payment.dto.PaymentDTO;
import com.learning.payment.entity.PaymentDO;
import com.learning.payment.feign.OrderFeignClient;
import com.learning.payment.mapper.PaymentMapper;
import com.learning.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * 支付服务实现类 - Seata分布式事务
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentMapper paymentMapper;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private CacheHelper cacheHelper;

    /**
     * 使用AT模式分布式事务
     * 订单创建成功后，创建支付记录
     */
    @Override
    @Transactional
    public Long createPayment(CreatePaymentRequest request) {
        // 1. 检查订单是否存在
        Result<Object> orderResult = orderFeignClient.getOrderById(request.getOrderId(), "mock-token");
        if (orderResult == null || orderResult.getCode() != 200) {
            throw new BusinessException("订单不存在");
        }

        // 2. 创建支付记录
        PaymentDO payment = new PaymentDO();
        payment.setUserId(request.getUserId());
        payment.setOrderId(request.getOrderId());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setAmount(request.getAmount());
        payment.setStatus(0); // 未支付

        paymentMapper.createPayment(payment);

        // 3. 缓存支付记录
        cacheHelper.set("payment:" + payment.getPaymentId(), payment, 3600L, TimeUnit.SECONDS);

        log.info("支付记录创建成功: paymentId={}, orderId={}, amount={}",
            payment.getPaymentId(), request.getOrderId(), request.getAmount());

        return payment.getPaymentId();
    }

    /**
     * 调用第三方支付接口（模拟）
     */
    @Override
    public void callThirdPartyPayment(Long paymentId) {
        PaymentDO payment = paymentMapper.selectById(paymentId);
        if (payment == null || payment.getDeleted() == 1) {
            throw new BusinessException("支付记录不存在");
        }

        if (payment.getStatus() != 0) {
            throw new BusinessException("支付状态异常");
        }

        // 模拟调用第三方支付接口
        try {
            // 模拟网络请求延迟
            Thread.sleep(1000);

            // 模拟第三方支付成功
            String transactionId = "TXN" + System.currentTimeMillis();
            String channelCode = "SUCCESS";
            String channelMessage = "支付成功";

            // 更新支付记录
            paymentMapper.updatePaymentChannelInfo(paymentId, transactionId, channelCode, channelMessage);

            // 更新缓存
            payment.setStatus(1); // 已支付
            payment.setTransactionId(transactionId);
            cacheHelper.set("payment:" + paymentId, payment, 3600L, TimeUnit.SECONDS);

            log.info("第三方支付成功: paymentId={}, transactionId={}", paymentId, transactionId);

            // 通知订单服务更新订单状态
            orderFeignClient.payOrder(
                payment.getOrderId(),
                transactionId,
                payment.getPaymentMethod(),
                "mock-token"
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BusinessException("支付处理异常");
        } catch (Exception e) {
            log.error("第三方支付失败: paymentId={}", paymentId, e);
            paymentMapper.updatePaymentStatus(paymentId, 2); // 处理中
            throw new BusinessException("支付处理失败");
        }
    }

    @Override
    public PaymentDTO getPaymentById(Long paymentId) {
        PaymentDO payment = paymentMapper.selectById(paymentId);
        if (payment == null || payment.getDeleted() == 1) {
            throw new BusinessException("支付记录不存在");
        }

        PaymentDTO dto = convertToPaymentDTO(payment);
        return dto;
    }

    @Override
    public PaymentDTO getPaymentByOrderId(Long orderId) {
        PaymentDO payment = paymentMapper.selectByOrderId(orderId);
        if (payment == null || payment.getDeleted() == 1) {
            throw new BusinessException("支付记录不存在");
        }

        PaymentDTO dto = convertToPaymentDTO(payment);
        return dto;
    }

    @Override
    public Result<Object> getUserPayments(Long userId, Integer page, Integer size) {
        // TODO: 调用订单服务查询用户的支付记录
        return Result.success(new Object());
    }

    @Override
    public PaymentDTO getUnpaidPayment(Long userId) {
        PaymentDO payment = paymentMapper.selectUnpaidByUserId(userId);
        if (payment == null) {
            return null;
        }
        return convertToPaymentDTO(payment);
    }

    /**
     * 转换为PaymentDTO
     */
    private PaymentDTO convertToPaymentDTO(PaymentDO payment) {
        PaymentDTO dto = new PaymentDTO();
        BeanUtils.copyProperties(payment, dto);

        // 支付方式转换
        dto.setPaymentMethodText(getPaymentMethodText(payment.getPaymentMethod()));
        dto.setStatusText(getStatusText(payment.getStatus()));
        dto.setRefundStatusText(getRefundStatusText(payment.getRefundStatus()));

        return dto;
    }

    private String getPaymentMethodText(Integer paymentMethod) {
        switch (paymentMethod) {
            case 1: return "支付宝";
            case 2: return "微信支付";
            case 3: return "余额支付";
            default: return "未知";
        }
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "未支付";
            case 1: return "已支付";
            case 2: return "处理中";
            default: return "未知";
        }
    }

    private String getRefundStatusText(Integer refundStatus) {
        switch (refundStatus) {
            case 0: return "未退款";
            case 1: return "退款中";
            case 2: return "已退款";
            default: return "未知";
        }
    }
}
