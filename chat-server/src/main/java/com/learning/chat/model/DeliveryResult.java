package com.learning.chat.model;

import lombok.Data;

@Data
public class DeliveryResult {
    private boolean success;
    private String reason;
    private String deliveredTo;
    private Long deliveredCount;
}
