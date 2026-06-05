package com.learning.admin.dto;

import lombok.Data;

@Data
public class LogOverviewStatisticsDTO {
    private long operationLogCount;
    private long auditLogCount;
    private long errorLogCount;
    private long recent24hErrorCount;
}
