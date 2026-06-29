package com.learning.admin.service.impl;

import com.learning.admin.document.ErrorLogDO;
import com.learning.admin.dto.CreateErrorLogRequest;
import com.learning.admin.dto.ErrorLogDTO;
import com.learning.admin.dto.ErrorLogQueryRequest;
import com.learning.admin.repository.ErrorLogRepository;
import com.learning.admin.service.ErrorLogService;
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
public class ErrorLogServiceImpl implements ErrorLogService {

    private final ErrorLogRepository errorLogRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public void createLog(CreateErrorLogRequest request) {
        ErrorLogDO log = new ErrorLogDO();
        BeanUtils.copyProperties(request, log);
        log.setCreateTime(LocalDateTime.now());
        errorLogRepository.save(log);
    }

    @Override
    public PageResult<ErrorLogDTO> pageLogs(ErrorLogQueryRequest request) {
        Query query = new Query();
        if (StringUtils.hasText(request.getServiceName())) {
            query.addCriteria(Criteria.where("serviceName").is(request.getServiceName()));
        }
        if (StringUtils.hasText(request.getSeverity())) {
            query.addCriteria(Criteria.where("severity").is(request.getSeverity()));
        }
        if (StringUtils.hasText(request.getBusinessType())) {
            query.addCriteria(Criteria.where("businessType").is(request.getBusinessType()));
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

        List<ErrorLogDO> logs = mongoTemplate.find(query, ErrorLogDO.class);
        long total = mongoTemplate.count(countQuery, ErrorLogDO.class);
        List<ErrorLogDTO> records = logs.stream().map(this::toDTO).toList();
        return PageResult.of(records, total, request.getCurrent().longValue(), request.getSize().longValue());
    }

    private ErrorLogDTO toDTO(ErrorLogDO log) {
        ErrorLogDTO dto = new ErrorLogDTO();
        BeanUtils.copyProperties(log, dto);
        return dto;
    }

    private LocalDateTime parseTime(String time) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
