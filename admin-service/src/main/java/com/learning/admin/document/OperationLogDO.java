package com.learning.admin.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "operation_logs")
@CompoundIndex(name = "idx_operation_service_time", def = "{'serviceName': 1, 'createTime': -1}")
public class OperationLogDO {

    @Id
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
