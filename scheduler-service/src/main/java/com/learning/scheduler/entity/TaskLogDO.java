package com.learning.scheduler.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务日志实体类（MongoDB）
 */
@Data
public class TaskLogDO implements Serializable {

    /**
     * 任务ID
     */
    private String jobId;

    /**
     * 任务执行器ID
     */
    private String executorId;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务参数
     */
    private String jobParam;

    /**
     * 执行状态
     * 0-成功 1-失败
     */
    private Integer status;

    /**
     * 执行结果
     */
    private String handleResult;

    /**
     * 执行时间（毫秒）
     */
    private Long executeTime;

    /**
     * 异常信息
     */
    private String handleMsg;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    private Integer deleted;
}
