package com.learning.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 取消订单请求
 */
@Data
public class CancelOrderRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 取消原因
     */
    @NotNull(message = "取消原因不能为空")
    private String cancelReason;
}
