package com.learning.message.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 支付相关事件
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 支付ID
     */
    private Long paymentId;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 支付状态
     */
    private Integer paymentStatus;

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
     * 支付成功事件
     */
    public static PaymentEvent success(Long paymentId, Long orderId, Long userId, String title, String content) {
        return new PaymentEvent("PAYMENT_SUCCESS", paymentId, orderId, userId, 1, content, paymentId.toString(), title, 2, false);
    }

    /**
     * 支付失败事件
     */
    public static PaymentEvent failed(Long paymentId, Long orderId, Long userId, String title, String content) {
        return new PaymentEvent("PAYMENT_FAILED", paymentId, orderId, userId, 0, content, paymentId.toString(), title, 2, true);
    }

    /**
     * 支付超时事件
     */
    public static PaymentEvent timeout(Long paymentId, Long orderId, Long userId, String title, String content) {
        return new PaymentEvent("PAYMENT_TIMEOUT", paymentId, orderId, userId, 0, content, paymentId.toString(), title, 2, true);
    }

    /**
     * 退款成功事件
     */
    public static PaymentEvent refundSuccess(Long paymentId, Long orderId, Long userId, String title, String content) {
        return new PaymentEvent("REFUND_SUCCESS", paymentId, orderId, userId, 2, content, paymentId.toString(), title, 1, false);
    }

    /**
     * 退款失败事件
     */
    public static PaymentEvent refundFailed(Long paymentId, Long orderId, Long userId, String title, String content) {
        return new PaymentEvent("REFUND_FAILED", paymentId, orderId, userId, 3, content, paymentId.toString(), title, 2, true);
    }
}
