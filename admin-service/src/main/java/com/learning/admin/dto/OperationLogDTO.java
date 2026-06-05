package com.learning.admin.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OperationLogDTO {
    private String logId;
    private String serviceName;
    private Long operatorId;
    private String operatorName;
    private String operationType;
    private String businessType;
    private String businessId;
    private String requestPath;
    private String requestMethod;
    private String requestParam;
    private Integer resultStatus;
    private String resultMessage;
    private String ip;
    private LocalDateTime createTime;
}
