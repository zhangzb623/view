package com.learning.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 创建消息请求
 */
@Data
public class CreateMessageRequest {

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /**
     * 消息类型
     * 1-订单通知 2-支付通知 3-退款通知 4-系统通知
     */
    @NotNull(message = "消息类型不能为空")
    private Integer messageType;

    /**
     * 消息标题
     */
    @NotBlank(message = "消息标题不能为空")
    private String title;

    /**
     * 消息内容
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 是否重要
     */
    private Integer important;

    /**
     * 关联业务ID
     */
    private String businessId;

    /**
     * 关联业务类型
     */
    private String businessType;

    /**
     * 消息来源
     */
    private Integer source;
}
