package com.learning.admin.service.impl;

import com.learning.admin.document.AuditLogDO;
import com.learning.admin.dto.AuditLogDTO;
import com.learning.admin.dto.AuditLogQueryRequest;
import com.learning.admin.dto.CreateAuditLogRequest;
import com.learning.admin.repository.AuditLogRepository;
import com.learning.admin.service.AuditLogService;
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
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public void createLog(CreateAuditLogRequest request) {
        AuditLogDO log = new AuditLogDO();
        BeanUtils.copyProperties(request, log);
        log.setCreateTime(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    @Override
    public PageResult<AuditLogDTO> pageLogs(AuditLogQueryRequest request) {
        Query query = new Query();
        if (StringUtils.hasText(request.getServiceName())) {
            query.addCriteria(Criteria.where("serviceName").is(request.getServiceName()));
        }
        if (StringUtils.hasText(request.getBusinessType())) {
            query.addCriteria(Criteria.where("businessType").is(request.getBusinessType()));
        }
        if (StringUtils.hasText(request.getBusinessId())) {
            query.addCriteria(Criteria.where("businessId").is(request.getBusinessId()));
        }
        if (StringUtils.hasText(request.getAction())) {
            query.addCriteria(Criteria.where("action").is(request.getAction()));
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

        List<AuditLogDO> logs = mongoTemplate.find(query, AuditLogDO.class);
        long total = mongoTemplate.count(countQuery, AuditLogDO.class);
        List<AuditLogDTO> records = logs.stream().map(this::toDTO).toList();
        return PageResult.of(records, total, request.getCurrent().longValue(), request.getSize().longValue());
    }

    private AuditLogDTO toDTO(AuditLogDO log) {
        AuditLogDTO dto = new AuditLogDTO();
        BeanUtils.copyProperties(log, dto);
        return dto;
    }

    private LocalDateTime parseTime(String time) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
