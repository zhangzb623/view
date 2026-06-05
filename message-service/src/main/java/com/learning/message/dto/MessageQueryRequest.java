package com.learning.message.dto;

import lombok.Data;

/**
 * 消息查询请求
 */
@Data
public class MessageQueryRequest {

    private Long userId;
    private Integer messageType;
    private Integer status;
    private String businessType;
    private Integer page = 1;
    private Integer size = 10;
}
