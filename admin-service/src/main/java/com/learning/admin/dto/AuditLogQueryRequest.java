package com.learning.admin.dto;

import lombok.Data;

@Data
public class AuditLogQueryRequest {
    private String serviceName;
    private String businessType;
    private String businessId;
    private String action;
    private String startTime;
    private String endTime;
    private Integer current = 1;
    private Integer size = 10;
}
