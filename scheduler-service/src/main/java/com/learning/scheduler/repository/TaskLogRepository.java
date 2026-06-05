package com.learning.scheduler.repository;

import com.learning.scheduler.entity.TaskLogDO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 任务日志Repository接口
 */
@Repository
public interface TaskLogRepository extends MongoRepository<TaskLogDO, String> {

    /**
     * 根据任务名称查询日志
     */
    List<TaskLogDO> findByJobNameAndDeleted(String jobName, Integer deleted);

    /**
     * 根据执行状态查询日志
     */
    List<TaskLogDO> findByStatusAndDeleted(Integer status, Integer deleted);

    /**
     * 根据任务名称和执行状态查询
     */
    List<TaskLogDO> findByJobNameAndStatusAndDeleted(String jobName, Integer status, Integer deleted);

    /**
     * 根据任务名称和创建时间范围查询
     */
    List<TaskLogDO> findByJobNameAndCreateTimeBetween(String jobName, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 统计任务执行次数
     */
    long countByJobNameAndDeleted(String jobName, Integer deleted);

    /**
     * 统计成功次数
     */
    long countByJobNameAndStatusAndDeleted(String jobName, Integer status, Integer deleted);

    /**
     * 统计失败次数
     */
    long countByJobNameAndStatusAndDeleted(String jobName, Integer status, Integer deleted);
}
