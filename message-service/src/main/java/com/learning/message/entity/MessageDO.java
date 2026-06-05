package com.learning.message.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息实体类
 */
@Data
@TableName("t_message")
public class MessageDO implements Serializable {

    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 消息类型
     * 1-订单通知 2-支付通知 3-退款通知 4-系统通知
     */
    private Integer messageType;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息状态
     * 0-未读 1-已读 2-已删除
     */
    private Integer status;

    /**
     * 是否重要
     */
    private Integer important;

    /**
     * 关联业务ID（订单ID、支付ID等）
     */
    private String businessId;

    /**
     * 关联业务类型（order、payment、refund、system）
     */
    private String businessType;

    /**
     * 消息来源
     * 1-order-service 2-payment-service 3-system
     */
    private Integer source;

    /**
     * 是否已发送通知
     */
    private Integer notified;

    /**
     * 通知时间
     */
    private LocalDateTime notifyTime;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    @TableLogic
    @TableField(value = "deleted")
    private Integer deleted;
}
