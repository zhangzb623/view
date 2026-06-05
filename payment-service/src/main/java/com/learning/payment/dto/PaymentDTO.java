package com.learning.payment.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付DTO
 */
@Data
public class PaymentDTO implements Serializable {

    private Long paymentId;
    private Long userId;
    private String username;
    private Long orderId;
    private Integer paymentMethod;
    private String paymentMethodText;
    private BigDecimal amount;
    private Integer status;
    private String statusText;
    private String transactionId;
    private String channelCode;
    private String channelMessage;
    private BigDecimal refundAmount;
    private Integer refundStatus;
    private String refundStatusText;
    private String refundReason;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
