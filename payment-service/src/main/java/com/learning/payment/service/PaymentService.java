package com.learning.payment.service;

import com.learning.common.api.result.Result;
import com.learning.payment.dto.CreatePaymentRequest;
import com.learning.payment.dto.PaymentDTO;
import com.learning.payment.dto.RefundDTO;

/**
 * 支付服务接口
 */
public interface PaymentService {

    /**
     * 创建支付
     */
    Long createPayment(CreatePaymentRequest request);

    /**
     * 调用第三方支付（模拟）
     */
    void callThirdPartyPayment(Long paymentId);

    /**
     * 根据ID查询支付
     */
    PaymentDTO getPaymentById(Long paymentId);

    /**
     * 根据订单ID查询支付
     */
    PaymentDTO getPaymentByOrderId(Long orderId);

    /**
     * 查询用户支付记录列表
     */
    Result<Object> getUserPayments(Long userId, Integer page, Integer size);

    /**
     * 查询用户未支付支付记录
     */
    PaymentDTO getUnpaidPayment(Long userId);
}
