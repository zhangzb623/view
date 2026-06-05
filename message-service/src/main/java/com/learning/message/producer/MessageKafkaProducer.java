package com.learning.message.producer;

import com.learning.message.event.OrderEvent;
import com.learning.message.event.PaymentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 消息Kafka生产者
 */
@Slf4j
@Component
public class MessageKafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 发送订单事件
     */
    public CompletableFuture<SendResult<String, Object>> sendOrderEvent(OrderEvent event) {
        String topic = getTopic(event.getEventType());
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event);

        future.thenAccept(result -> {
            log.info("订单事件已发送: eventType={}, orderId={}, userId={}",
                event.getEventType(), event.getOrderId(), event.getUserId());
        }).exceptionally(ex -> {
            log.error("订单事件发送失败: eventType={}", event.getEventType(), ex);
            return null;
        });

        return future;
    }

    /**
     * 发送支付事件
     */
    public CompletableFuture<SendResult<String, Object>> sendPaymentEvent(PaymentEvent event) {
        String topic = getTopic(event.getEventType());
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, event);

        future.thenAccept(result -> {
            log.info("支付事件已发送: eventType={}, paymentId={}, userId={}",
                event.getEventType(), event.getPaymentId(), event.getUserId());
        }).exceptionally(ex -> {
            log.error("支付事件发送失败: eventType={}", event.getEventType(), ex);
            return null;
        });

        return future;
    }

    /**
     * 发送系统事件
     */
    public CompletableFuture<SendResult<String, Object>> sendSystemEvent(String eventType, Object data) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send("system." + eventType, data);

        future.thenAccept(result -> {
            log.info("系统事件已发送: eventType={}", eventType);
        }).exceptionally(ex -> {
            log.error("系统事件发送失败: eventType={}", eventType, ex);
            return null;
        });

        return future;
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
