package com.learning.admin.service.impl;

import com.learning.admin.document.AuditLogDO;
import com.learning.admin.document.ErrorLogDO;
import com.learning.admin.document.OperationLogDO;
import com.learning.admin.dto.ErrorTrendPointDTO;
import com.learning.admin.dto.LogOverviewStatisticsDTO;
import com.learning.admin.dto.ServiceLogRankDTO;
import com.learning.admin.repository.AuditLogRepository;
import com.learning.admin.repository.ErrorLogRepository;
import com.learning.admin.repository.OperationLogRepository;
import com.learning.admin.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final OperationLogRepository operationLogRepository;
    private final AuditLogRepository auditLogRepository;
    private final ErrorLogRepository errorLogRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public LogOverviewStatisticsDTO getOverview() {
        LogOverviewStatisticsDTO dto = new LogOverviewStatisticsDTO();
        dto.setOperationLogCount(operationLogRepository.count());
        dto.setAuditLogCount(auditLogRepository.count());
        dto.setErrorLogCount(errorLogRepository.count());
        dto.setRecent24hErrorCount(
            mongoTemplate.count(
                Query.query(Criteria.where("createTime").gte(LocalDateTime.now().minusHours(24))),
                ErrorLogDO.class
            )
        );
        return dto;
    }

    @Override
    public List<ErrorTrendPointDTO> getErrorTrend() {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("createTime").gte(LocalDateTime.now().minusHours(24))),
            Aggregation.project()
                .andExpression("dateToString('%Y-%m-%d %H:00:00', createTime)")
                .as("timeBucket"),
            Aggregation.group("timeBucket").count().as("count"),
            Aggregation.sort(Sort.by(Sort.Direction.ASC, "_id"))
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "error_logs", Document.class);
        List<ErrorTrendPointDTO> points = new ArrayList<>();
        for (Document document : results.getMappedResults()) {
            ErrorTrendPointDTO dto = new ErrorTrendPointDTO();
            dto.setTimeBucket(document.getString("_id"));
            dto.setCount(document.get("count", Number.class).longValue());
            points.add(dto);
        }
        return points;
    }

    @Override
    public List<ServiceLogRankDTO> getServiceRank() {
        Map<String, Long> operationCounts = aggregateCounts("operation_logs");
        Map<String, Long> auditCounts = aggregateCounts("audit_logs");
        Map<String, Long> errorCounts = aggregateCounts("error_logs");

        Map<String, ServiceLogRankDTO> rankMap = new HashMap<>();
        mergeRank(rankMap, operationCounts, auditCounts, errorCounts);

        return rankMap.values().stream()
            .sorted(Comparator.comparingLong(ServiceLogRankDTO::getTotalCount).reversed())
            .toList();
    }

    private Map<String, Long> aggregateCounts(String collectionName) {
        Aggregation aggregation = Aggregation.newAggregation(
            Aggregation.group("serviceName").count().as("count")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, collectionName, Document.class);
        Map<String, Long> counts = new HashMap<>();
        for (Document document : results.getMappedResults()) {
            counts.put(document.getString("_id"), document.get("count", Number.class).longValue());
        }
        return counts;
    }

    private void mergeRank(Map<String, ServiceLogRankDTO> rankMap,
                           Map<String, Long> operationCounts,
                           Map<String, Long> auditCounts,
                           Map<String, Long> errorCounts) {
        for (String serviceName : unionKeys(operationCounts, auditCounts, errorCounts)) {
            long operationCount = operationCounts.getOrDefault(serviceName, 0L);
            long auditCount = auditCounts.getOrDefault(serviceName, 0L);
            long errorCount = errorCounts.getOrDefault(serviceName, 0L);

            ServiceLogRankDTO dto = new ServiceLogRankDTO();
            dto.setServiceName(serviceName);
            dto.setTotalCount(operationCount + auditCount + errorCount);
            dto.setErrorCount(errorCount);
            rankMap.put(serviceName, dto);
        }
    }

    private List<String> unionKeys(Map<String, Long> operationCounts,
                                   Map<String, Long> auditCounts,
                                   Map<String, Long> errorCounts) {
        List<String> keys = new ArrayList<>();
        keys.addAll(operationCounts.keySet());
        for (String key : auditCounts.keySet()) {
            if (!keys.contains(key)) {
                keys.add(key);
            }
        }
        for (String key : errorCounts.keySet()) {
            if (!keys.contains(key)) {
                keys.add(key);
            }
        }
        return keys;
    }
}
