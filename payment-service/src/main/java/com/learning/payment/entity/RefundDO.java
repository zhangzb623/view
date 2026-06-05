package com.learning.payment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款记录实体类
 */
@Data
@TableName("t_refund")
public class RefundDO implements Serializable {

    @TableId(value = "refund_id", type = IdType.AUTO)
    private Long refundId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 支付记录ID
     */
    private Long paymentId;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 退款状态
     * 0-待处理 1-处理中 2-成功 3-失败
     */
    private Integer status;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 退款申请时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime applyTime;

    /**
     * 退款完成时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime completeTime;

    /**
     * 退款渠道
     */
    private String refundChannel;

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
