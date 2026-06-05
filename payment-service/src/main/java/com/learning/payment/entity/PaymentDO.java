package com.learning.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付记录实体类
 */
@Data
@TableName("t_payment")
public class PaymentDO implements Serializable {

    @TableId(value = "payment_id", type = IdType.AUTO)
    private Long paymentId;

    /**
     * 用户ID - 分片键
     */
    private Long userId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付方式
     * 1-支付宝 2-微信支付 3-余额支付
     */
    private Integer paymentMethod;

    /**
     * 支付金额
     */
    private BigDecimal amount;

    /**
     * 支付状态
     * 0-未支付 1-已支付 2-处理中
     */
    private Integer status;

    /**
     * 支付渠道流水号（第三方返回）
     */
    private String transactionId;

    /**
     * 支付渠道返回码
     */
    private String channelCode;

    /**
     * 支付渠道返回消息
     */
    private String channelMessage;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款状态
     * 0-未退款 1-退款中 2-已退款
     */
    private Integer refundStatus;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款渠道流水号
     */
    private String refundTransactionId;

    /**
     * 备注
     */
    private String remark;

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
