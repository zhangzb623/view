package com.learning.payment.feign;

import lombok.Data;

/**
 * 取消订单请求（Feign用）
 */
@Data
public class CancelOrderRequest {
    private Long orderId;
    private String cancelReason;
}
