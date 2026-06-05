package com.learning.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建支付请求
 */
@Data
public class CreatePaymentRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 支付方式
     * 1-支付宝 2-微信支付 3-余额支付
     */
    @NotNull(message = "支付方式不能为空")
    private Integer paymentMethod;

    /**
     * 支付金额
     */
    @NotNull(message = "支付金额不能为空")
    private BigDecimal amount;

    /**
     * 退款原因
     */
    private String refundReason;
}
