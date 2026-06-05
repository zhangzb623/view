package com.learning.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorLogDTO {
    private String errorId;
    private String serviceName;
    private String businessType;
    private String businessId;
    private String errorCode;
    private String errorMessage;
    private String stackSummary;
    private String traceId;
    private String severity;
    private LocalDateTime createTime;
}
