package com.learning.message.consumer;

import com.learning.message.entity.MessageDO;
import com.learning.message.event.OrderEvent;
import com.learning.message.mapper.MessageMapper;
import com.learning.message.producer.MessageRocketMQProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 订单RocketMQ消费者
 */
@Slf4j
@Component
@RocketMQMessageListener(
    topic = "order.created",
    consumerGroup = "message-order-consumer",
    selectorExpression = "*"
)
public class OrderRocketMQConsumer implements RocketMQListener<OrderEvent> {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageRocketMQProducer rocketMQProducer;

    @Override
    public void onMessage(OrderEvent event) {
        try {
            log.info("接收到订单事件: eventType={}, orderId={}, userId={}",
                event.getEventType(), event.getOrderId(), event.getUserId());

            // 创建消息
            createMessage(event);

            // 发送延迟消息（用于订单超时）
            if ("ORDER_CREATED".equals(event.getEventType())) {
                rocketMQProducer.sendOrderDelayMessage(event.getOrderId(), event.getUserId(), "30s");
            }

        } catch (Exception e) {
            log.error("处理订单事件失败: eventType={}", event.getEventType(), e);
            // 抛出异常，消息会重新投递
            throw new RuntimeException(e);
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
