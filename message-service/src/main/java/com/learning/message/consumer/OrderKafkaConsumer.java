package com.learning.message.consumer;

import com.learning.message.entity.MessageDO;
import com.learning.message.event.OrderEvent;
import com.learning.message.mapper.MessageMapper;
import com.learning.message.producer.MessageKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单Kafka消费者
 */
@Slf4j
@Component
public class OrderKafkaConsumer {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageKafkaProducer kafkaProducer;

    /**
     * 订单创建事件
     */
    @KafkaListener(topics = "order.created", groupId = "message-service")
    public void consumeOrderCreated(@Payload OrderEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("接收到订单创建事件: orderId={}, userId={}", event.getOrderId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 确认消费
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("处理订单创建事件失败: orderId={}", event.getOrderId(), e);
            // 抛出异常，消息会重新投递
            throw e;
        }
    }

    /**
     * 订单支付事件
     */
    @KafkaListener(topics = "order.paid", groupId = "message-service")
    public void consumeOrderPaid(@Payload OrderEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("接收到订单支付事件: orderId={}, userId={}", event.getOrderId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 确认消费
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("处理订单支付事件失败: orderId={}", event.getOrderId(), e);
            throw e;
        }
    }

    /**
     * 订单完成事件
     */
    @KafkaListener(topics = "order.completed", groupId = "message-service")
    public void consumeOrderCompleted(@Payload OrderEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("接收到订单完成事件: orderId={}, userId={}", event.getOrderId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 确认消费
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("处理订单完成事件失败: orderId={}", event.getOrderId(), e);
            throw e;
        }
    }

    /**
     * 订单取消事件
     */
    @KafkaListener(topics = "order.cancelled", groupId = "message-service")
    public void consumeOrderCancelled(@Payload OrderEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("接收到订单取消事件: orderId={}, userId={}", event.getOrderId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 确认消费
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("处理订单取消事件失败: orderId={}", event.getOrderId(), e);
            throw e;
        }
    }

    /**
     * 订单发货事件
     */
    @KafkaListener(topics = "order.shipped", groupId = "message-service")
    public void consumeOrderShipped(@Payload OrderEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("接收到订单发货事件: orderId={}, userId={}", event.getOrderId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 确认消费
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("处理订单发货事件失败: orderId={}", event.getOrderId(), e);
            throw e;
        }
    }

    /**
     * 订单已签收事件
     */
    @KafkaListener(topics = "order.received", groupId = "message-service")
    public void consumeOrderReceived(@Payload OrderEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("接收到订单已签收事件: orderId={}, userId={}", event.getOrderId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 确认消费
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("处理订单已签收事件失败: orderId={}", event.getOrderId(), e);
            throw e;
        }
    }

    /**
     * 创建消息
     */
    private void createMessage(OrderEvent event) {
        MessageDO message = new MessageDO();
        message.setUserId(event.getUserId());
        message.setMessageType(event.getMessageType());
        message.setTitle(event.getTitle());
        message.setContent(event.getMessage());
        message.setImportant(event.getImportant() ? 1 : 0);
        message.setBusinessId(event.getBusinessId());
        message.setBusinessType("order");
        message.setSource(1); // order-service
        message.setStatus(0); // 未读

        messageMapper.insert(message);

        // 异步发送通知
        sendNotificationAsync(message);
    }

    /**
     * 异步发送通知
     */
    private void sendNotificationAsync(MessageDO message) {
        // TODO: 发送WebSocket通知、邮件通知、短信通知等
        log.info("消息通知已发送: messageId={}, userId={}, title={}",
            message.getMessageId(), message.getUserId(), message.getTitle());
    }
}
