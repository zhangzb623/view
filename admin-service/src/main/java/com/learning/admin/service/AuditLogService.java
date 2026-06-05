package com.learning.admin.service;

import com.learning.admin.dto.AuditLogDTO;
import com.learning.admin.dto.AuditLogQueryRequest;
import com.learning.admin.dto.CreateAuditLogRequest;
import com.learning.common.api.result.PageResult;

public interface AuditLogService {
    void createLog(CreateAuditLogRequest request);
    PageResult<AuditLogDTO> pageLogs(AuditLogQueryRequest request);
}
