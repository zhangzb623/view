package com.learning.order.producer;

import com.learning.order.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * 订单Kafka生产者
 */
@Slf4j
@Component
public class OrderKafkaProducer {

    @Autowired
    private KafkaTemplate<String, OrderEvent> kafkaTemplate;

    /**
     * 发送订单创建事件
     */
    public CompletableFuture<SendResult<String, OrderEvent>> sendOrderCreatedEvent(OrderEvent event) {
        CompletableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(
            "order.created",
            event.getUserId().toString(),
            event
        );

        future.thenAccept(result -> {
            log.info("订单创建事件已发送: orderId={}, userId={}",
                event.getOrderId(), event.getUserId());
        }).exceptionally(ex -> {
            log.error("订单创建事件发送失败: orderId={}", event.getOrderId(), ex);
            return null;
        });

        return future;
    }

    /**
     * 发送订单支付事件
     */
    public CompletableFuture<SendResult<String, OrderEvent>> sendOrderPaidEvent(OrderEvent event) {
        CompletableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(
            "order.paid",
            event.getUserId().toString(),
            event
        );

        future.thenAccept(result -> {
            log.info("订单支付事件已发送: orderId={}, userId={}",
                event.getOrderId(), event.getUserId());
        }).exceptionally(ex -> {
            log.error("订单支付事件发送失败: orderId={}", event.getOrderId(), ex);
            return null;
        });

        return future;
    }

    /**
     * 发送订单取消事件
     */
    public CompletableFuture<SendResult<String, OrderEvent>> sendOrderCancelledEvent(OrderEvent event) {
        CompletableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(
            "order.cancelled",
            event.getUserId().toString(),
            event
        );

        future.thenAccept(result -> {
            log.info("订单取消事件已发送: orderId={}, userId={}",
                event.getOrderId(), event.getUserId());
        }).exceptionally(ex -> {
            log.error("订单取消事件发送失败: orderId={}", event.getOrderId(), ex);
            return null;
        });

        return future;
    }

    /**
     * 发送订单完成事件
     */
    public CompletableFuture<SendResult<String, OrderEvent>> sendOrderCompletedEvent(OrderEvent event) {
        CompletableFuture<SendResult<String, OrderEvent>> future = kafkaTemplate.send(
            "order.completed",
            event.getUserId().toString(),
            event
        );

        future.thenAccept(result -> {
            log.info("订单完成事件已发送: orderId={}, userId={}",
                event.getOrderId(), event.getUserId());
        }).exceptionally(ex -> {
            log.error("订单完成事件发送失败: orderId={}", event.getOrderId(), ex);
            return null;
        });

        return future;
    }
}
