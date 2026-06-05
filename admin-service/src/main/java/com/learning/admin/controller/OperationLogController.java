package com.learning.admin.controller;

import com.learning.admin.dto.CreateOperationLogRequest;
import com.learning.admin.dto.OperationLogDTO;
import com.learning.admin.dto.OperationLogQueryRequest;
import com.learning.admin.service.OperationLogService;
import com.learning.common.api.result.PageResult;
import com.learning.common.api.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs/operation")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody CreateOperationLogRequest request) {
        operationLogService.createLog(request);
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<OperationLogDTO>> page(OperationLogQueryRequest request) {
        return Result.success(operationLogService.pageLogs(request));
    }
}
