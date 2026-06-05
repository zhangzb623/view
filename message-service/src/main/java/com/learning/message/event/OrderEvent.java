package com.learning.message.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 订单相关事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单状态
     */
    private Integer orderStatus;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 关联业务ID
     */
    private String businessId;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息类型
     */
    private Integer messageType;

    /**
     * 是否重要
     */
    private Boolean important;

    /**
     * 订单创建事件
     */
    public static OrderEvent created(Long orderId, Long userId, String title, String content) {
        return new OrderEvent("ORDER_CREATED", orderId, userId, 1, content, orderId.toString(), title, 1, false);
    }

    /**
     * 订单支付事件
     */
    public static OrderEvent paid(Long orderId, Long userId, String title, String content) {
        return new OrderEvent("ORDER_PAID", orderId, userId, 2, content, orderId.toString(), title, 2, false);
    }

    /**
     * 订单完成事件
     */
    public static OrderEvent completed(Long orderId, Long userId, String title, String content) {
        return new OrderEvent("ORDER_COMPLETED", orderId, userId, 3, content, orderId.toString(), title, 1, false);
    }

    /**
     * 订单取消事件
     */
    public static OrderEvent cancelled(Long orderId, Long userId, String title, String content) {
        return new OrderEvent("ORDER_CANCELLED", orderId, userId, 4, content, orderId.toString(), title, 2, false);
    }

    /**
     * 订单发货事件
     */
    public static OrderEvent shipped(Long orderId, Long userId, String title, String content) {
        return new OrderEvent("ORDER_SHIPPED", orderId, userId, 2, content, orderId.toString(), title, 1, false);
    }

    /**
     * 订单已签收事件
     */
    public static OrderEvent received(Long orderId, Long userId, String title, String content) {
        return new OrderEvent("ORDER_RECEIVED", orderId, userId, 3, content, orderId.toString(), title, 1, true);
    }
}
