package com.learning.message.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息DTO
 */
@Data
public class MessageDTO implements Serializable {

    private Long messageId;
    private Long userId;
    private Integer messageType;
    private String messageTypeText;
    private String title;
    private String content;
    private Integer status;
    private String statusText;
    private Integer important;
    private String businessId;
    private String businessType;
    private String businessTypeText;
    private Integer source;
    private String sourceText;
    private Integer notified;
    private LocalDateTime notifyTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
