package com.learning.admin.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "error_logs")
@CompoundIndex(name = "idx_error_service_time", def = "{'serviceName': 1, 'createTime': -1}")
public class ErrorLogDO {

    @Id
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
