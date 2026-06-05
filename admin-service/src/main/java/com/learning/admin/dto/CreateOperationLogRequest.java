package com.learning.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOperationLogRequest {

    @NotBlank(message = "serviceName不能为空")
    private String serviceName;
    private Long operatorId;
    private String operatorName;
    @NotBlank(message = "operationType不能为空")
    private String operationType;
    @NotBlank(message = "businessType不能为空")
    private String businessType;
    private String businessId;
    private String requestPath;
    private String requestMethod;
    private String requestParam;
    private Integer resultStatus;
    private String resultMessage;
    private String ip;
}
