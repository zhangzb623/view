package com.learning.order.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类 - 演示ShardingJDBC分片
 */
@Data
@TableName("t_order")
public class OrderDO implements Serializable {

    @TableId(value = "order_id", type = IdType.AUTO)
    private Long orderId;

    /**
     * 用户ID - 分片键
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 单价
     */
    private BigDecimal unitPrice;

    /**
     * 总金额
     */
    private BigDecimal totalPrice;

    /**
     * 订单状态
     * 0-待支付 1-待发货 2-待收货 3-已完成 4-已取消 5-退款中 6-已退款
     */
    private Integer status;

    /**
     * 支付方式
     * 1-支付宝 2-微信支付 3-余额支付
     */
    private Integer paymentMethod;

    /**
     * 支付状态
     * 0-未支付 1-已支付
     */
    private Integer paymentStatus;

    /**
     * 交易流水号
     */
    private String transactionId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 收货电话
     */
    private String receiverPhone;

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
