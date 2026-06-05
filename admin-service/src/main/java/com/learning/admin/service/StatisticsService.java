package com.learning.admin.service;

import com.learning.admin.dto.ErrorTrendPointDTO;
import com.learning.admin.dto.LogOverviewStatisticsDTO;
import com.learning.admin.dto.ServiceLogRankDTO;

import java.util.List;

public interface StatisticsService {
    LogOverviewStatisticsDTO getOverview();
    List<ErrorTrendPointDTO> getErrorTrend();
    List<ServiceLogRankDTO> getServiceRank();
}
