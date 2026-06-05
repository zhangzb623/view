package com.learning.payment.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款DTO
 */
@Data
public class RefundDTO implements Serializable {

    private Long refundId;
    private Long userId;
    private Long orderId;
    private Long paymentId;
    private BigDecimal refundAmount;
    private Integer status;
    private String statusText;
    private String refundReason;
    private LocalDateTime applyTime;
    private LocalDateTime completeTime;
    private String refundChannel;
    private String refundTransactionId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
