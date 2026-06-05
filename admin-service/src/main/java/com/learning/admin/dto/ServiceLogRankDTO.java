package com.learning.admin.dto;

import lombok.Data;

@Data
public class ServiceLogRankDTO {
    private String serviceName;
    private long totalCount;
    private long errorCount;
}
