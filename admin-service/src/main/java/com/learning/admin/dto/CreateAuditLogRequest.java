package com.learning.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAuditLogRequest {

    @NotBlank(message = "serviceName不能为空")
    private String serviceName;
    @NotBlank(message = "businessType不能为空")
    private String businessType;
    @NotBlank(message = "businessId不能为空")
    private String businessId;
    private String beforeStatus;
    private String afterStatus;
    @NotBlank(message = "action不能为空")
    private String action;
    private String reason;
    private Long operatorId;
    private String traceId;
}
