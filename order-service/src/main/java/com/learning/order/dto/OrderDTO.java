package com.learning.order.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单DTO
 */
@Data
public class OrderDTO implements Serializable {

    private Long orderId;
    private Long userId;
    private String username;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Integer status;
    private String statusText;
    private Integer paymentMethod;
    private String paymentMethodText;
    private Integer paymentStatus;
    private String paymentStatusText;
    private String transactionId;
    private String remark;
    private String address;
    private String receiver;
    private String receiverPhone;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
