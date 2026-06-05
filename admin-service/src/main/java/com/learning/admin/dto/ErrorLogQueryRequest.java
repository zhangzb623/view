package com.learning.admin.dto;

import lombok.Data;

@Data
public class ErrorLogQueryRequest {
    private String serviceName;
    private String severity;
    private String businessType;
    private String startTime;
    private String endTime;
    private Integer current = 1;
    private Integer size = 10;
}
