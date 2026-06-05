package com.learning.admin.dto;

import lombok.Data;

@Data
public class OperationLogQueryRequest {
    private String serviceName;
    private Long operatorId;
    private String businessType;
    private String businessId;
    private String startTime;
    private String endTime;
    private Integer current = 1;
    private Integer size = 10;
}
