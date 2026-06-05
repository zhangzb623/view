package com.learning.scheduler.service.impl;

import com.learning.common.starter.exception.BusinessException;
import com.learning.scheduler.dto.TaskLogDTO;
import com.learning.scheduler.dto.TaskQueryRequest;
import com.learning.scheduler.entity.TaskLogDO;
import com.learning.scheduler.repository.TaskLogRepository;
import com.learning.scheduler.service.TaskLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 任务日志服务实现类
 */
@Slf4j
@Service
public class TaskLogServiceImpl implements TaskLogService {

    @Autowired
    private TaskLogRepository taskLogRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Transactional
    public TaskLogDTO createTaskLog(TaskLogDTO taskLogDTO) {
        TaskLogDO taskLog = new TaskLogDO();
        BeanUtils.copyProperties(taskLogDTO, taskLog);

        // 设置默认值
        if (taskLog.getStatus() == null) {
            taskLog.setStatus(0);
        }
        if (taskLog.getDeleted() == null) {
            taskLog.setDeleted(0);
        }
        if (taskLog.getExecuteTime() == null) {
            taskLog.setExecuteTime(0L);
        }

        TaskLogDO saved = taskLogRepository.save(taskLog);

        return convertToTaskLogDTO(saved);
    }

    @Override
    public TaskLogDTO getTaskLogById(String jobId) {
        TaskLogDO taskLog = taskLogRepository.findById(jobId).orElse(null);
        if (taskLog == null || taskLog.getDeleted() == 1) {
            throw new BusinessException("任务日志不存在");
        }
        return convertToTaskLogDTO(taskLog);
    }

    @Override
    public List<TaskLogDTO> getLogsByJobName(String jobName) {
        List<TaskLogDO> taskLogs = taskLogRepository.findByJobNameAndDeleted(jobName, 0);
        return taskLogs.stream()
                .map(this::convertToTaskLogDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskLogDTO> getLogsByStatus(Integer status) {
        List<TaskLogDO> taskLogs = taskLogRepository.findByStatusAndDeleted(status, 0);
        return taskLogs.stream()
                .map(this::convertToTaskLogDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TaskLogDTO> getLogsByPage(TaskQueryRequest request) {
        // 构建查询条件
        Query query = new Query();
        query.addCriteria(Criteria.where("deleted").is(0));

        if (request.getJobName() != null && !request.getJobName().isEmpty()) {
            query.addCriteria(Criteria.where("jobName").is(request.getJobName()));
        }

        if (request.getStatus() != null) {
            query.addCriteria(Criteria.where("status").is(request.getStatus()));
        }

        // 排序
        query.with(PageRequest.of(request.getPage() - 1, request.getSize()))
              .orderBy(org.springframework.data.domain.Sort.by(
                  org.springframework.data.domain.Sort.Direction.DESC, "createTime")
              );

        List<TaskLogDO> taskLogs = mongoTemplate.find(query, TaskLogDO.class);

        return taskLogs.stream()
                .map(this::convertToTaskLogDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long countLogsByJobName(String jobName) {
        return taskLogRepository.countByJobNameAndDeleted(jobName, 0);
    }

    @Override
    public long countSuccessByJobName(String jobName) {
        return taskLogRepository.countByJobNameAndStatusAndDeleted(jobName, 0, 0);
    }

    @Override
    public long countFailByJobName(String jobName) {
        return taskLogRepository.countByJobNameAndStatusAndDeleted(jobName, 1, 0);
    }

    @Override
    public Map<String, Object> getTaskStatistics(String jobName) {
        long totalCount = countLogsByJobName(jobName);
        long successCount = countSuccessByJobName(jobName);
        long failCount = countFailByJobName(jobName);
        double successRate = totalCount > 0 ? (double) successCount / totalCount * 100 : 0;

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("jobName", jobName);
        statistics.put("totalCount", totalCount);
        statistics.put("successCount", successCount);
        statistics.put("failCount", failCount);
        statistics.put("successRate", String.format("%.2f%%", successRate));

        // 获取最新的执行时间
        Query query = new Query();
        query.addCriteria(Criteria.where("jobName").is(jobName));
        query.limit(1);
        query.with(PageRequest.of(0, 1));

        List<TaskLogDO> logs = mongoTemplate.find(query, TaskLogDO.class);
        if (!logs.isEmpty()) {
            statistics.put("lastExecuteTime", logs.get(0).getCreateTime());
        } else {
            statistics.put("lastExecuteTime", null);
        }

        return statistics;
    }

    /**
     * 更新任务日志状态
     */
    public void updateTaskLogStatus(String jobId, Integer status, String handleMsg) {
        Query query = new Query(Criteria.where("_id").is(jobId));
        Update update = new Update()
                .set("status", status)
                .set("handleMsg", handleMsg)
                .set("updateTime", LocalDateTime.now());

        mongoTemplate.updateFirst(query, update, TaskLogDO.class);
    }

    /**
     * 设置任务执行时间
     */
    public void setTaskExecuteTime(String jobId) {
        Query query = new Query(Criteria.where("_id").is(jobId));
        Update update = new Update()
                .set("executeTime", System.currentTimeMillis() - 0L);

        mongoTemplate.updateFirst(query, update, TaskLogDO.class);
    }

    /**
     * 转换为TaskLogDTO
     */
    private TaskLogDTO convertToTaskLogDTO(TaskLogDO taskLog) {
        TaskLogDTO dto = new TaskLogDTO();
        BeanUtils.copyProperties(taskLog, dto);

        dto.setStatusText(getStatusText(taskLog.getStatus()));
        dto.setCreateTime(LocalDateTime.parse(taskLog.getCreateTime().toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        return dto;
    }

    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "成功";
            case 1: return "失败";
            default: return "未知";
        }
    }
}
