package com.learning.message.producer;

import com.learning.message.event.OrderEvent;
import com.learning.message.event.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 消息RocketMQ生产者
 */
@Slf4j
@Component
public class MessageRocketMQProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送订单事件
     */
    public void sendOrderEvent(OrderEvent event) {
        String topic = getTopic(event.getEventType());

        Message<OrderEvent> message = MessageBuilder
            .withPayload(event)
            .build();

        rocketMQTemplate.syncSend(topic, message);

        log.info("订单事件已发送: eventType={}, orderId={}, userId={}",
            event.getEventType(), event.getOrderId(), event.getUserId());
    }

    /**
     * 发送支付事件
     */
    public void sendPaymentEvent(PaymentEvent event) {
        String topic = getTopic(event.getEventType());

        Message<PaymentEvent> message = MessageBuilder
            .withPayload(event)
            .build();

        rocketMQTemplate.syncSend(topic, message);

        log.info("支付事件已发送: eventType={}, paymentId={}, userId={}",
            event.getEventType(), event.getPaymentId(), event.getUserId());
    }

    /**
     * 发送系统事件
     */
    public void sendSystemEvent(String eventType, Object data) {
        rocketMQTemplate.syncSend("system." + eventType, data);

        log.info("系统事件已发送: eventType={}", eventType);
    }

    /**
     * 发送延迟消息（用于订单超时）
     */
    public void sendOrderDelayMessage(Long orderId, Long userId, String delayTime) {
        Message<OrderEvent> message = MessageBuilder
            .withPayload(new OrderEvent("ORDER_TIMEOUT", orderId, userId, 4, "订单超时", orderId.toString(),
                "订单超时", 2, true))
            .build();

        // 延迟级别：1=1s, 2=5s, 3=10s, 4=30s, 5=1min, 6=2min, 7=3min, 8=4min, 9=5min, 10=10min, 11=20min, 12=30min, 13=1h, 14=2h, 15=3h
        rocketMQTemplate.syncSend("order.delay", message, 3000, 4);

        log.info("订单延迟消息已发送: orderId={}, delayLevel=4 (30秒)", orderId);
    }

    /**
     * 根据事件类型获取Topic
     */
    private String getTopic(String eventType) {
        switch (eventType) {
            case "ORDER_CREATED":
                return "order.created";
            case "ORDER_PAID":
                return "order.paid";
            case "ORDER_COMPLETED":
                return "order.completed";
            case "ORDER_CANCELLED":
                return "order.cancelled";
            case "ORDER_SHIPPED":
                return "order.shipped";
            case "ORDER_RECEIVED":
                return "order.received";
            case "PAYMENT_SUCCESS":
                return "payment.success";
            case "PAYMENT_FAILED":
                return "payment.failed";
            case "PAYMENT_TIMEOUT":
                return "payment.timeout";
            case "REFUND_SUCCESS":
                return "payment.refund.success";
            case "REFUND_FAILED":
                return "payment.refund.failed";
            default:
                return "system." + eventType;
        }
    }
}
