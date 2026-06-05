package com.learning.admin.controller;

import com.learning.admin.dto.AuditLogDTO;
import com.learning.admin.dto.AuditLogQueryRequest;
import com.learning.admin.dto.CreateAuditLogRequest;
import com.learning.admin.service.AuditLogService;
import com.learning.common.api.result.PageResult;
import com.learning.common.api.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs/audit")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody CreateAuditLogRequest request) {
        auditLogService.createLog(request);
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<AuditLogDTO>> page(AuditLogQueryRequest request) {
        return Result.success(auditLogService.pageLogs(request));
    }
}
