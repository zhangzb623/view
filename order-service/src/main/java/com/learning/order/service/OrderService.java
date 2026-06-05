package com.learning.order.service;

import com.learning.common.api.result.PageResult;
import com.learning.order.dto.*;

/**
 * 订单服务接口
 */
public interface OrderService {

    /**
     * 创建订单
     */
    Long createOrder(CreateOrderRequest request, Long userId) throws Exception;

    /**
     * 支付订单
     */
    Boolean payOrder(Long orderId, String transactionId, Integer paymentMethod);

    /**
     * 取消订单
     */
    Boolean cancelOrder(CancelOrderRequest request) throws Exception;

    /**
     * 根据订单ID查询订单
     */
    OrderDTO getOrderByOrderId(Long orderId) throws Exception;

    /**
     * 根据用户ID查询订单列表（分页）
     */
    PageResult<OrderDTO> getOrdersByUserId(OrderQueryRequest request);

    /**
     * 更新订单状态
     */
    Boolean updateOrderStatus(Long orderId, Integer status);

    /**
     * 发货
     */
    Boolean shipOrder(Long orderId, String trackingNumber);

    /**
     * 完成订单
     */
    Boolean completeOrder(Long orderId);
}
