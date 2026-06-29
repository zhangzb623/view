package com.learning.order.producer;

import com.learning.order.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

/**
 * 订单RocketMQ生产者
 */
@Slf4j
@Component
public class OrderRocketMQProducer {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送订单延迟取消消息（30分钟超时）
     */
    public void sendOrderCancelDelayMessage(Long orderId, Long userId) {
        Message<OrderEvent> message = MessageBuilder
            .withPayload(new OrderEvent(orderId, userId, "CANCELLED", null, null, null, null, null, null))
            .setHeader("DELAY_TIME_LEVEL", 4)
            .build();

        rocketMQTemplate.syncSend("order-cancel-delay-topic", message);

        log.info("订单延迟取消消息已发送: orderId={}, delayLevel=4 (30分钟)", orderId);
    }

    /**
     * 发送订单支付成功通知（同步）
     */
    public void sendOrderPaymentSuccess(Long orderId, Long userId) {
        Message<OrderEvent> message = MessageBuilder
            .withPayload(new OrderEvent(orderId, userId, "PAYMENT_SUCCESS", null, null, null, null, null, null))
            .build();

        rocketMQTemplate.syncSend("order-payment-topic", message);

        log.info("订单支付成功通知已发送: orderId={}, userId={}", orderId, userId);
    }

    /**
     * 发送订单发货通知（同步）
     */
    public void sendOrderShipped(Long orderId, Long userId) {
        Message<OrderEvent> message = MessageBuilder
            .withPayload(new OrderEvent(orderId, userId, "SHIPPED", null, null, null, null, null, null))
            .build();

        rocketMQTemplate.syncSend("order-ship-topic", message);

        log.info("订单发货通知已发送: orderId={}, userId={}", orderId, userId);
    }
}
