package com.learning.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateErrorLogRequest {

    @NotBlank(message = "serviceName不能为空")
    private String serviceName;
    @NotBlank(message = "businessType不能为空")
    private String businessType;
    private String businessId;
    private String errorCode;
    @NotBlank(message = "errorMessage不能为空")
    private String errorMessage;
    private String stackSummary;
    private String traceId;
    @NotBlank(message = "severity不能为空")
    private String severity;
}
