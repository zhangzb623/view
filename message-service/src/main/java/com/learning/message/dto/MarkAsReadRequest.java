package com.learning.message.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 标记已读请求
 */
@Data
public class MarkAsReadRequest {

    /**
     * 消息ID
     */
    @NotNull(message = "消息ID不能为空")
    private Long messageId;
}
