package com.learning.admin.controller;

import com.learning.admin.dto.CreateErrorLogRequest;
import com.learning.admin.dto.ErrorLogDTO;
import com.learning.admin.dto.ErrorLogQueryRequest;
import com.learning.admin.service.ErrorLogService;
import com.learning.common.api.result.PageResult;
import com.learning.common.api.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs/error")
public class ErrorLogController {

    private final ErrorLogService errorLogService;

    public ErrorLogController(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody CreateErrorLogRequest request) {
        errorLogService.createLog(request);
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<ErrorLogDTO>> page(ErrorLogQueryRequest request) {
        return Result.success(errorLogService.pageLogs(request));
    }
}
