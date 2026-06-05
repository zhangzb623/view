package com.learning.scheduler.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 任务日志DTO
 */
@Data
public class TaskLogDTO implements Serializable {

    private String jobId;
    private String executorId;
    private String jobName;
    private String jobParam;
    private Integer status;
    private String statusText;
    private String handleResult;
    private Long executeTime;
    private String handleMsg;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
