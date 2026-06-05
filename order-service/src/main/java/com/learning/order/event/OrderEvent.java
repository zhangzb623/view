package com.learning.order.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 事件类型
     * CREATED-订单创建, PAID-订单支付, CANCELLED-订单取消, COMPLETED-订单完成
     */
    private String eventType;

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
     * 备注
     */
    private String remark;

    /**
     * 订单创建事件
     */
    public static OrderEvent created(Long orderId, Long userId, Long productId, String productName,
                                     Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
        return new OrderEvent(orderId, userId, "CREATED", productId, productName,
                             quantity, unitPrice, totalPrice, "订单创建成功");
    }

    /**
     * 订单支付事件
     */
    public static OrderEvent paid(Long orderId, Long userId, String transactionId) {
        return new OrderEvent(orderId, userId, "PAID", null, null, null, null, null, transactionId);
    }

    /**
     * 订单取消事件
     */
    public static OrderEvent cancelled(Long orderId, Long userId, String cancelReason) {
        return new OrderEvent(orderId, userId, "CANCELLED", null, null, null, null, null, cancelReason);
    }

    /**
     * 订单完成事件
     */
    public static OrderEvent completed(Long orderId, Long userId) {
        return new OrderEvent(orderId, userId, "COMPLETED", null, null, null, null, null, null);
    }
}
