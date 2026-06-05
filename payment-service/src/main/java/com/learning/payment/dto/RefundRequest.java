package com.learning.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 退款请求
 */
@Data
public class RefundRequest {

    /**
     * 支付记录ID
     */
    @NotNull(message = "支付记录ID不能为空")
    private Long paymentId;

    /**
     * 退款原因
     */
    @NotNull(message = "退款原因不能为空")
    private String refundReason;

    /**
     * 退款金额（可选，默认全额退款）
     */
    private BigDecimal refundAmount;
}
