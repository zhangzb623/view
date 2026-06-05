package com.learning.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 更新支付状态请求
 */
@Data
public class PaymentStatusRequest {

    /**
     * 支付ID
     */
    @NotNull(message = "支付ID不能为空")
    private Long paymentId;

    /**
     * 支付状态
     */
    @NotNull(message = "支付状态不能为空")
    private Integer status;
}
