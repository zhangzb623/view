package com.learning.scheduler.controller;

import com.learning.common.api.result.Result;
import com.learning.common.starter.exception.BusinessException;
import com.learning.scheduler.dto.TaskLogDTO;
import com.learning.scheduler.dto.TaskQueryRequest;
import com.learning.scheduler.service.TaskLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 任务日志控制器
 */
@Slf4j
@Tag(name = "任务日志管理", description = "任务日志CRUD、统计等接口")
@RestController
@RequestMapping("/api/task-log")
public class TaskLogController {

    @Autowired
    private TaskLogService taskLogService;

    @Operation(summary = "查询任务日志", description = "分页查询任务日志")
    @GetMapping("/list")
    public Result<List<TaskLogDTO>> getTaskLogs(
            @Parameter(description = "任务名称") @RequestParam(required = false) String jobName,
            @Parameter(description = "执行状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            TaskQueryRequest request = new TaskQueryRequest();
            request.setJobName(jobName);
            request.setStatus(status);
            request.setPage(page);
            request.setSize(size);

            List<TaskLogDTO> logs = taskLogService.getLogsByPage(request);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询任务日志失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "根据任务名称查询日志", description = "查询指定任务的执行日志")
    @GetMapping("/job/{jobName}")
    public Result<List<TaskLogDTO>> getLogsByJobName(
            @Parameter(description = "任务名称") @PathVariable String jobName) {
        try {
            List<TaskLogDTO> logs = taskLogService.getLogsByJobName(jobName);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询任务日志失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "查询成功日志", description = "查询任务成功的执行日志")
    @GetMapping("/success")
    public Result<List<TaskLogDTO>> getSuccessLogs() {
        try {
            List<TaskLogDTO> logs = taskLogService.getLogsByStatus(0);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询成功日志失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "查询失败日志", description = "查询任务失败的执行日志")
    @GetMapping("/fail")
    public Result<List<TaskLogDTO>> getFailLogs() {
        try {
            List<TaskLogDTO> logs = taskLogService.getLogsByStatus(1);
            return Result.success(logs);
        } catch (Exception e) {
            log.error("查询失败日志失败", e);
            return Result.fail("查询失败");
        }
    }

    @Operation(summary = "统计任务执行次数", description = "统计指定任务的执行次数")
    @GetMapping("/count/{jobName}")
    public Result<Long> countLogsByJobName(
            @Parameter(description = "任务名称") @PathVariable String jobName) {
        try {
            long count = taskLogService.countLogsByJobName(jobName);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计失败", e);
            return Result.fail("统计失败");
        }
    }

    @Operation(summary = "统计任务成功次数", description = "统计指定任务的成功次数")
    @GetMapping("/count/success/{jobName}")
    public Result<Long> countSuccessByJobName(
            @Parameter(description = "任务名称") @PathVariable String jobName) {
        try {
            long count = taskLogService.countSuccessByJobName(jobName);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计失败", e);
            return Result.fail("统计失败");
        }
    }

    @Operation(summary = "统计任务失败次数", description = "统计指定任务的失败次数")
    @GetMapping("/count/fail/{jobName}")
    public Result<Long> countFailByJobName(
            @Parameter(description = "任务名称") @PathVariable String jobName) {
        try {
            long count = taskLogService.countFailByJobName(jobName);
            return Result.success(count);
        } catch (Exception e) {
            log.error("统计失败", e);
            return Result.fail("统计失败");
        }
    }

    @Operation(summary = "获取任务统计信息", description = "获取指定任务的详细统计信息")
    @GetMapping("/statistics/{jobName}")
    public Result<Map<String, Object>> getTaskStatistics(
            @Parameter(description = "任务名称") @PathVariable String jobName) {
        try {
            Map<String, Object> statistics = taskLogService.getTaskStatistics(jobName);
            return Result.success(statistics);
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return Result.fail("获取失败");
        }
    }
}
