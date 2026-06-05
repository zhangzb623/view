package com.learning.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogDTO {
    private String auditId;
    private String serviceName;
    private String businessType;
    private String businessId;
    private String beforeStatus;
    private String afterStatus;
    private String action;
    private String reason;
    private Long operatorId;
    private String traceId;
    private LocalDateTime createTime;
}
