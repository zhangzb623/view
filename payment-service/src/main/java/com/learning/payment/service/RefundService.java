package com.learning.payment.service;

import com.learning.payment.dto.RefundDTO;
import com.learning.payment.dto.RefundRequest;

/**
 * 退款服务接口
 */
public interface RefundService {

    /**
     * 创建退款
     */
    Long createRefund(RefundRequest request);

    /**
     * 调用第三方退款（模拟）
     */
    void callThirdPartyRefund(Long refundId);

    /**
     * 根据ID查询退款
     */
    RefundDTO getRefundById(Long refundId);

    /**
     * 根据订单ID查询退款
     */
    RefundDTO getRefundByOrderId(Long orderId);

    /**
     * 查询订单的退款记录
     */
    java.util.List<RefundDTO> getOrderRefunds(Long orderId);
}
