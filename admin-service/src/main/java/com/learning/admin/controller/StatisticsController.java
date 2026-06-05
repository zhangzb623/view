package com.learning.admin.controller;

import com.learning.admin.dto.ErrorTrendPointDTO;
import com.learning.admin.dto.LogOverviewStatisticsDTO;
import com.learning.admin.dto.ServiceLogRankDTO;
import com.learning.admin.service.StatisticsService;
import com.learning.common.api.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/logs/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    public Result<LogOverviewStatisticsDTO> overview() {
        return Result.success(statisticsService.getOverview());
    }

    @GetMapping("/error-trend")
    public Result<List<ErrorTrendPointDTO>> errorTrend() {
        return Result.success(statisticsService.getErrorTrend());
    }

    @GetMapping("/service-rank")
    public Result<List<ServiceLogRankDTO>> serviceRank() {
        return Result.success(statisticsService.getServiceRank());
    }
}
