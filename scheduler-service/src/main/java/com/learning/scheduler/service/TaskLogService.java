package com.learning.scheduler.service;

import com.learning.scheduler.dto.TaskLogDTO;
import com.learning.scheduler.dto.TaskQueryRequest;

import java.util.List;
import java.util.Map;

/**
 * 任务日志服务接口
 */
public interface TaskLogService {

    /**
     * 创建任务日志
     */
    TaskLogDTO createTaskLog(TaskLogDTO taskLog);

    /**
     * 根据ID查询任务日志
     */
    TaskLogDTO getTaskLogById(String jobId);

    /**
     * 根据任务名称查询日志
     */
    List<TaskLogDTO> getLogsByJobName(String jobName);

    /**
     * 根据执行状态查询日志
     */
    List<TaskLogDTO> getLogsByStatus(Integer status);

    /**
     * 分页查询任务日志
     */
    List<TaskLogDTO> getLogsByPage(TaskQueryRequest request);

    /**
     * 统计任务执行次数
     */
    long countLogsByJobName(String jobName);

    /**
     * 统计成功次数
     */
    long countSuccessByJobName(String jobName);

    /**
     * 统计失败次数
     */
    long countFailByJobName(String jobName);

    /**
     * 获取任务统计信息
     */
    Map<String, Object> getTaskStatistics(String jobName);
}
