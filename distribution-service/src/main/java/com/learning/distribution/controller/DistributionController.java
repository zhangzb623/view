package com.learning.distribution.controller;

import com.learning.common.api.result.PageResult;
import com.learning.common.api.result.Result;
import com.learning.common.starter.exception.BusinessException;
import com.learning.distribution.dto.CreateDistributionRequest;
import com.learning.distribution.dto.DistributionDTO;
import com.learning.distribution.dto.DistributionQueryRequest;
import com.learning.distribution.service.DistributionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/distribution")
@RequiredArgsConstructor
@Tag(name = "分销管理", description = "分销佣金计算与结算接口")
public class DistributionController {

    private final DistributionService distributionService;

    @PostMapping("/calculate")
    @Operation(summary = "计算分销记录", description = "根据订单生成分销佣金记录")
    public Result<Long> calculate(@Valid @RequestBody CreateDistributionRequest request) {
        try {
            return Result.success("分销记录创建成功", distributionService.calculate(request));
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("计算分销记录失败", e);
            return Result.fail("计算分销记录失败");
        }
    }

    @GetMapping("/{distributionId}")
    @Operation(summary = "查询分销详情", description = "根据分销ID查询详情")
    public Result<DistributionDTO> getById(@Parameter(description = "分销ID") @PathVariable Long distributionId) {
        try {
            return Result.success(distributionService.getById(distributionId));
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("查询分销详情失败", e);
            return Result.fail("查询失败");
        }
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "按订单查询分销记录", description = "根据订单ID查询分销记录")
    public Result<DistributionDTO> getByOrderId(@Parameter(description = "订单ID") @PathVariable Long orderId) {
        try {
            return Result.success(distributionService.getByOrderId(orderId));
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("按订单查询分销记录失败", e);
            return Result.fail("查询失败");
        }
    }

    @GetMapping("/user/{distributorUserId}/list")
    @Operation(summary = "查询分销列表", description = "按分销用户查询分销记录列表")
    public Result<PageResult<DistributionDTO>> getByDistributor(
            @Parameter(description = "分销用户ID") @PathVariable Long distributorUserId,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        try {
            DistributionQueryRequest request = new DistributionQueryRequest();
            request.setDistributorUserId(distributorUserId);
            request.setStatus(status);
            request.setCurrent(current);
            request.setSize(size);
            return Result.success(distributionService.getByDistributor(request));
        } catch (Exception e) {
            log.error("查询分销列表失败", e);
            return Result.fail("查询失败");
        }
    }

    @PostMapping("/{distributionId}/settle")
    @Operation(summary = "结算分销记录", description = "将分销记录标记为已结算，可选开启回滚演示")
    public Result<Void> settle(
            @Parameter(description = "分销ID") @PathVariable Long distributionId,
            @Parameter(description = "是否模拟回滚") @RequestParam(defaultValue = "false") boolean simulateRollback) {
        try {
            distributionService.settle(distributionId, simulateRollback);
            return Result.success(simulateRollback ? "已触发回滚演示" : "结算成功", null);
        } catch (BusinessException e) {
            return Result.fail(e.getCode(), e.getMessage());
        } catch (Exception e) {
            log.error("结算分销记录失败", e);
            return Result.fail("结算失败");
        }
    }
}
