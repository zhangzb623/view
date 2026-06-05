package com.learning.message.consumer;

import com.learning.message.entity.MessageDO;
import com.learning.message.event.PaymentEvent;
import com.learning.message.mapper.MessageMapper;
import com.learning.message.producer.MessageKafkaProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 支付Kafka消费者
 */
@Slf4j
@Component
public class PaymentKafkaConsumer {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageKafkaProducer kafkaProducer;

    /**
     * 支付成功事件
     */
    @KafkaListener(topics = "payment.success", groupId = "message-service")
    public void consumePaymentSuccess(@Payload PaymentEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("接收到支付成功事件: paymentId={}, userId={}", event.getPaymentId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 确认消费
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("处理支付成功事件失败: paymentId={}", event.getPaymentId(), e);
            throw e;
        }
    }

    /**
     * 支付失败事件
     */
    @KafkaListener(topics = "payment.failed", groupId = "message-service")
    public void consumePaymentFailed(@Payload PaymentEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("接收到支付失败事件: paymentId={}, userId={}", event.getPaymentId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 确认消费
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("处理支付失败事件失败: paymentId={}", event.getPaymentId(), e);
            throw e;
        }
    }

    /**
     * 支付超时事件
     */
    @KafkaListener(topics = "payment.timeout", groupId = "message-service")
    public void consumePaymentTimeout(@Payload PaymentEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("接收到支付超时事件: paymentId={}, userId={}", event.getPaymentId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 确认消费
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("处理支付超时事件失败: paymentId={}", event.getPaymentId(), e);
            throw e;
        }
    }

    /**
     * 退款成功事件
     */
    @KafkaListener(topics = "payment.refund.success", groupId = "message-service")
    public void consumeRefundSuccess(@Payload PaymentEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("接收到退款成功事件: paymentId={}, userId={}", event.getPaymentId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 确认消费
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("处理退款成功事件失败: paymentId={}", event.getPaymentId(), e);
            throw e;
        }
    }

    /**
     * 退款失败事件
     */
    @KafkaListener(topics = "payment.refund.failed", groupId = "message-service")
    public void consumeRefundFailed(@Payload PaymentEvent event, Acknowledgment acknowledgment) {
        try {
            log.info("接收到退款失败事件: paymentId={}, userId={}", event.getPaymentId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 确认消费
            acknowledgment.acknowledge();
        } catch (Exception e) {
            log.error("处理退款失败事件失败: paymentId={}", event.getPaymentId(), e);
            throw e;
        }
    }

    /**
     * 创建消息
     */
    private void createMessage(PaymentEvent event) {
        MessageDO message = new MessageDO();
        message.setUserId(event.getUserId());
        message.setMessageType(event.getMessageType());
        message.setTitle(event.getTitle());
        message.setContent(event.getMessage());
        message.setImportant(event.getImportant() ? 1 : 0);
        message.setBusinessId(event.getBusinessId());
        message.setBusinessType("payment");
        message.setSource(2); // payment-service
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
