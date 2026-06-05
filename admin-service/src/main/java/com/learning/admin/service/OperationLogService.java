package com.learning.admin.service;

import com.learning.admin.dto.CreateOperationLogRequest;
import com.learning.admin.dto.OperationLogDTO;
import com.learning.admin.dto.OperationLogQueryRequest;
import com.learning.common.api.result.PageResult;

public interface OperationLogService {
    void createLog(CreateOperationLogRequest request);
    PageResult<OperationLogDTO> pageLogs(OperationLogQueryRequest request);
}
