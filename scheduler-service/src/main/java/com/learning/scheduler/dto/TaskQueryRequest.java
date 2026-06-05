package com.learning.scheduler.dto;

import lombok.Data;

/**
 * 任务查询请求
 */
@Data
public class TaskQueryRequest {

    private String jobName;
    private Integer status;
    private Integer page = 1;
    private Integer size = 10;
}
