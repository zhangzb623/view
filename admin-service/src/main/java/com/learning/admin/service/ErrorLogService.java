package com.learning.admin.service;

import com.learning.admin.dto.CreateErrorLogRequest;
import com.learning.admin.dto.ErrorLogDTO;
import com.learning.admin.dto.ErrorLogQueryRequest;
import com.learning.common.api.result.PageResult;

public interface ErrorLogService {
    void createLog(CreateErrorLogRequest request);
    PageResult<ErrorLogDTO> pageLogs(ErrorLogQueryRequest request);
}
