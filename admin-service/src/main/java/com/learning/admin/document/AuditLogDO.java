package com.learning.admin.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "audit_logs")
@CompoundIndex(name = "idx_audit_business_time", def = "{'businessType': 1, 'businessId': 1, 'createTime': -1}")
public class AuditLogDO {

    @Id
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
