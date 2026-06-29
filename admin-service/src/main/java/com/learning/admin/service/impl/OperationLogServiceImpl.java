package com.learning.admin.service.impl;

import com.learning.admin.document.OperationLogDO;
import com.learning.admin.dto.CreateOperationLogRequest;
import com.learning.admin.dto.OperationLogDTO;
import com.learning.admin.dto.OperationLogQueryRequest;
import com.learning.admin.repository.OperationLogRepository;
import com.learning.admin.service.OperationLogService;
import com.learning.common.api.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogRepository operationLogRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public void createLog(CreateOperationLogRequest request) {
        OperationLogDO log = new OperationLogDO();
        BeanUtils.copyProperties(request, log);
        log.setCreateTime(LocalDateTime.now());
        operationLogRepository.save(log);
    }

    @Override
    public PageResult<OperationLogDTO> pageLogs(OperationLogQueryRequest request) {
        Query query = new Query();
        if (StringUtils.hasText(request.getServiceName())) {
            query.addCriteria(Criteria.where("serviceName").is(request.getServiceName()));
        }
        if (request.getOperatorId() != null) {
            query.addCriteria(Criteria.where("operatorId").is(request.getOperatorId()));
        }
        if (StringUtils.hasText(request.getBusinessType())) {
            query.addCriteria(Criteria.where("businessType").is(request.getBusinessType()));
        }
        if (StringUtils.hasText(request.getBusinessId())) {
            query.addCriteria(Criteria.where("businessId").is(request.getBusinessId()));
        }
        if (StringUtils.hasText(request.getStartTime())) {
            query.addCriteria(Criteria.where("createTime").gte(parseTime(request.getStartTime())));
        }
        if (StringUtils.hasText(request.getEndTime())) {
            query.addCriteria(Criteria.where("createTime").lte(parseTime(request.getEndTime())));
        }
        query.with(Sort.by(Sort.Direction.DESC, "createTime"));

        Query countQuery = Query.of(query);
        query.skip((long) (request.getCurrent() - 1) * request.getSize());
        query.limit(request.getSize());

        List<OperationLogDO> logs = mongoTemplate.find(query, OperationLogDO.class);
        long total = mongoTemplate.count(countQuery, OperationLogDO.class);
        List<OperationLogDTO> records = logs.stream().map(this::toDTO).toList();
        return PageResult.of(records, total, request.getCurrent().longValue(), request.getSize().longValue());
    }

    private OperationLogDTO toDTO(OperationLogDO log) {
        OperationLogDTO dto = new OperationLogDTO();
        BeanUtils.copyProperties(log, dto);
        return dto;
    }

    private LocalDateTime parseTime(String time) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
