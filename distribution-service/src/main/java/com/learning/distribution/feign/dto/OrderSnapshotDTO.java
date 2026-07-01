package com.learning.distribution.feign.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSnapshotDTO {

    private Long orderId;
    private Long userId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private Integer status;
}
