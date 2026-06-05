# Message Service

消息服务，演示Kafka和RocketMQ消息队列以及消息通知功能。

## 功能列表

### 1. 消息管理
- ✅ 创建消息
- ✅ 查询消息详情
- ✅ 查询用户消息列表（分页）
- ✅ 标记消息为已读
- ✅ 标记所有消息为已读
- ✅ 删除消息（软删除）
- ✅ 统计未读消息

### 2. Kafka消息队列
- ✅ Kafka生产者
  - 订单事件：创建、支付、完成、取消、发货、签收
  - 支付事件：成功、失败、超时、退款
- ✅ Kafka消费者
  - 订单事件消费
  - 支付事件消费

### 3. RocketMQ消息队列
- ✅ RocketMQ生产者
  - 订单事件：创建、支付、完成、取消、发货、签收
  - 支付事件：成功、失败、超时、退款
  - 系统事件
- ✅ RocketMQ消费者
  - 订单事件消费
  - 支付事件消费
  - 延迟消息：订单超时检测

### 4. 事件系统
- ✅ OrderEvent：订单相关事件
  - ORDER_CREATED：订单创建
  - ORDER_PAID：订单支付
  - ORDER_COMPLETED：订单完成
  - ORDER_CANCELLED：订单取消
  - ORDER_SHIPPED：订单发货
  - ORDER_RECEIVED：订单签收
- ✅ PaymentEvent：支付相关事件
  - PAYMENT_SUCCESS：支付成功
  - PAYMENT_FAILED：支付失败
  - PAYMENT_TIMEOUT：支付超时
  - REFUND_SUCCESS：退款成功
  - REFUND_FAILED：退款失败

### 5. Redis缓存
- ✅ 消息详情缓存
- ✅ TTL: 1小时

## 技术实现

### 依赖技术
- **Spring Boot 3.2.0**: 基础框架
- **Spring Cloud**: 微服务架构
- **MyBatis Plus 3.5.5**: ORM框架
- **Kafka 3.5.1**: 消息队列
- **RocketMQ 5.1.0**: 消息队列
- **Redis**: 缓存
- **Common Domain**: 基础实体类
- **Common Starter**: 通用组件

### 核心功能实现

#### 1. 消息创建流程
```
订单服务创建订单
  → 发送OrderEvent到Kafka/RocketMQ
  → Message Service消费事件
    → 创建消息记录
      → 保存到数据库
        → Redis缓存
```

#### 2. Kafka生产者
```java
kafkaTemplate.send("order.created", orderEvent);
// 返回Future，异步发送
```

#### 3. Kafka消费者
```java
@KafkaListener(topics = "order.created", groupId = "message-service")
public void consumeOrderCreated(@Payload OrderEvent event) {
    createMessage(event);
    acknowledgment.acknowledge();
}
```

#### 4. RocketMQ生产者
```java
Message<OrderEvent> message = MessageBuilder
    .withPayload(orderEvent)
    .build();
rocketMQTemplate.syncSend("order.created", message);
```

#### 5. RocketMQ消费者
```java
@RocketMQMessageListener(
    topic = "order.created",
    consumerGroup = "message-order-consumer"
)
public class OrderRocketMQConsumer implements RocketMQListener<OrderEvent> {
    public void onMessage(OrderEvent event) {
        createMessage(event);
    }
}
```

## API接口

### 基础路径
`http://localhost:8086/api/message`

### 消息接口

#### 创建消息
```
POST /create
Content-Type: application/json

{
  "userId": 1,
  "messageType": 1,
  "title": "订单通知",
  "content": "您的订单已创建",
  "important": 1,
  "businessId": "1001",
  "businessType": "order",
  "source": 1
}
```

#### 查询消息详情
```
GET /{messageId}
```

#### 查询用户消息列表
```
GET /user/list?userId=1&page=1&size=10
```

#### 标记消息为已读
```
POST /{messageId}/read
```

#### 标记所有消息为已读
```
POST /user/{userId}/read-all
```

#### 删除消息
```
DELETE /{messageId}
```

#### 统计未读消息
```
GET /user/{userId}/unread/count
```

## 架构设计

### 服务架构
```
Message Service
├── Controller (MessageController) - 处理HTTP请求
├── Service (MessageService) - 业务逻辑
├── Mapper (MessageMapper) - 数据访问
├── Entity (MessageDO) - 数据库实体
├── DTO (CreateMessageRequest, MessageDTO, etc.) - 数据传输对象
├── Producer (MessageKafkaProducer, MessageRocketMQProducer) - 消息生产
├── Consumer (OrderKafkaConsumer, PaymentKafkaConsumer, etc.) - 消息消费
├── Event (OrderEvent, PaymentEvent) - 事件定义
└── Config (KafkaConfig, RocketMQConfig) - 消息队列配置
```

### Kafka架构
```
Order Service
  ↓ (OrderEvent)
Kafka Topics:
  - order.created
  - order.paid
  - order.completed
  - order.cancelled
  - order.shipped
  - order.received
  - payment.success
  - payment.failed
  - payment.timeout
  - payment.refund.success
  - payment.refund.failed
  ↓
Message Service
  ← (Consumer)
    创建消息记录
    → 数据库
```

### RocketMQ架构
```
Order Service
  ↓ (OrderEvent)
RocketMQ Topics:
  - order.created
  - order.paid
  - order.completed
  - order.cancelled
  - order.shipped
  - order.received
  - payment.success
  - payment.failed
  - payment.timeout
  - payment.refund.success
  - payment.refund.failed
  - order.delay (延迟消息)
  ↓
Message Service
  ← (Consumer)
    创建消息记录
    → 数据库
```

### 消息流程

#### 1. 订单创建流程
```
Order Service
  → OrderEvent.created()
  → MessageKafkaProducer.sendOrderEvent()
    → Kafka发送
  → MessageKafkaConsumer.consumeOrderCreated()
    → MessageMapper.insert()
    → updateMessageCache()
```

#### 2. 支付成功流程
```
Payment Service
  → PaymentEvent.success()
  → MessageKafkaProducer.sendPaymentEvent()
    → Kafka发送
  → PaymentKafkaConsumer.consumePaymentSuccess()
    → MessageMapper.insert()
    → updateMessageCache()
```

#### 3. 延迟消息流程
```
RocketMQ延迟消息 (30s)
  → OrderRocketMQConsumer.onMessage()
    → OrderEvent (ORDER_TIMEOUT)
    → MessageMapper.insert()
```

## 数据库表

### t_message (消息表)
| 字段 | 类型 | 说明 |
|------|------|------|
| message_id | BIGINT | 消息ID（主键，自增） |
| user_id | BIGINT | 用户ID |
| message_type | TINYINT | 消息类型（1订单 2支付 3退款 4系统） |
| title | VARCHAR(200) | 消息标题 |
| content | TEXT | 消息内容 |
| status | TINYINT | 状态（0未读 1已读 2已删除） |
| important | TINYINT | 是否重要 |
| business_id | VARCHAR(100) | 关联业务ID |
| business_type | VARCHAR(50) | 关联业务类型 |
| source | TINYINT | 消息来源 |
| notified | TINYINT | 是否已发送通知 |
| notify_time | DATETIME | 通知时间 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 删除标记 |

## Kafka配置

### Topic配置
```
order.created - 订单创建事件
order.paid - 订单支付事件
order.completed - 订单完成事件
order.cancelled - 订单取消事件
order.shipped - 订单发货事件
order.received - 订单签收事件
payment.success - 支付成功事件
payment.failed - 支付失败事件
payment.timeout - 支付超时事件
payment.refund.success - 退款成功事件
payment.refund.failed - 退款失败事件
```

### Kafka消费者配置
```yaml
spring:
  kafka:
    consumer:
      group-id: message-service
      auto-offset-reset: earliest
      enable-auto-commit: false
    listener:
      ack-mode: manual
      concurrency: 3
```

### 生产者配置
```yaml
spring:
  kafka:
    producer:
      acks: all
      retries: 3
```

## RocketMQ配置

### Topic配置
```
order.created - 订单创建事件
order.paid - 订单支付事件
order.completed - 订单完成事件
order.cancelled - 订单取消事件
order.shipped - 订单发货事件
order.received - 订单签收事件
payment.success - 支付成功事件
payment.failed - 支付失败事件
payment.timeout - 支付超时事件
payment.refund.success - 退款成功事件
payment.refund.failed - 退款失败事件
order.delay - 订单延迟消息
```

### 消费者配置
```java
@RocketMQMessageListener(
    topic = "order.created",
    consumerGroup = "message-order-consumer",
    selectorExpression = "*"
)
```

### 延迟消息配置
```java
// 延迟级别：1=1s, 2=5s, 3=10s, 4=30s, 5=1min, 6=2min, 7=3min, 8=4min, 9=5min, 10=10min, 11=20min, 12=30min, 13=1h, 14=2h, 15=3h
rocketMQTemplate.syncSend("order.delay", message, 3000, 4);
```

## 使用示例

### 创建消息
```bash
curl -X POST http://localhost:8086/api/message/create \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "messageType": 1,
    "title": "订单通知",
    "content": "您的订单已创建",
    "important": 1,
    "businessId": "1001",
    "businessType": "order",
    "source": 1
  }'

# 返回: {"code": 200, "message": "消息创建成功", "data": 1}
```

### 查询消息
```bash
curl -X GET http://localhost:8086/api/message/1
```

### 查询用户消息列表
```bash
curl -X GET "http://localhost:8086/api/message/user/list?userId=1&page=1&size=10"
```

### 标记消息为已读
```bash
curl -X POST http://localhost:8086/api/message/1/read
```

### 标记所有消息为已读
```bash
curl -X POST http://localhost:8086/api/message/user/1/read-all
```

### 统计未读消息
```bash
curl -X GET http://localhost:8086/api/message/user/1/unread/count

# 返回: {"code": 200, "message": "success", "data": 5}
```

## 注意事项

1. **消息可靠性**: Kafka使用异步发送，RocketMQ使用同步发送保证消息不丢失
2. **消息顺序**: 同一用户的同一类型消息按时间倒序排列
3. **消费者并发**: Kafka和RocketMQ都支持并发消费
4. **消息持久化**: Kafka和RocketMQ都提供消息持久化，重启不丢失
5. **重复消费**: 消息可能被重复消费，业务需要幂等性处理
6. **离线消息**: Kafka重启后可能丢失离线消息，RocketMQ支持离线消息
7. **延迟消息**: RocketMQ支持精确的延迟消息，Kafka需要高版本支持

## 扩展功能

- [ ] WebSocket实时推送
- [ ] 邮件通知
- [ ] 短信通知
- [ ] 消息模板
- [ ] 消息统计
- [ ] 消息推送集成
- [ ] 消息搜索

## 相关技术文档

- [Kafka官方文档](https://kafka.apache.org/documentation/)
- [RocketMQ官方文档](https://rocketmq.apache.org/docs/)
- [MyBatis Plus文档](https://baomidou.com/)
- [Spring Cloud文档](https://spring.io/projects/spring-cloud)

## 与其他服务的交互

### Order Service
- **通过消息队列**: 订单事件自动触发消息创建
- **无需直接调用**: 解耦服务，降低耦合

### Payment Service
- **通过消息队列**: 支付事件自动触发消息创建
- **无需直接调用**: 解耦服务，降低耦合

### Kafka Topics
- order.created, order.paid, order.completed等
- 由OrderService生产，MessageService消费

### RocketMQ Topics
- order.created, order.paid, order.completed等
- 由OrderService生产，MessageService消费

## Kafka部署

### 单机部署
```bash
# 下载Kafka
wget https://downloads.apache.org/kafka/3.5.1/kafka_2.13-3.5.1.tgz

# 解压并启动
tar -xzf kafka_2.13-3.5.1.tgz
cd kafka_2.13-3.5.1

# 启动Zookeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# 启动Kafka
bin/kafka-server-start.sh config/server.properties

# 创建Topic
bin/kafka-topics.sh --create \
  --bootstrap-server localhost:9092 \
  --replication-factor 1 \
  --partitions 3 \
  --topic order.created
```

### Docker部署
```bash
docker run -d \
  --name kafka \
  -p 9092:9092 \
  -p 9093:9093 \
  -p 9094:9094 \
  -e KAFKA_BROKER_ID=1 \
  -e KAFKA_ZOOKEEPER_CONNECT=localhost:2181 \
  -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:9092 \
  -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
  confluentinc/cp-kafka:7.5.0
```

## RocketMQ部署

### 单机部署
```bash
# 下载RocketMQ
wget https://archive.apache.org/dist/rocketmq/5.1.0/rocketmq-all-5.1.0-bin-release.zip

# 解压并启动
unzip rocketmq-all-5.1.0-bin-release.zip
cd rocketmq-all-5.1.0-bin-release

# 启动NameServer
bin/mqnamesrv

# 启动Broker
bin/mqbroker -n localhost:9876

# 创建Topic
sh tools.sh org.apache.rocketmq.tools.admin.CreateTopicCommand \
  -n localhost:9876 \
  -t order.created \
  -c 8
```

### Docker部署
```bash
docker run -d \
  --name rmqnamesrv \
  -p 9876:9876 \
  apache/rocketmq:5.1.0 \
  sh mqnamesrv

docker run -d \
  --name rmqbroker \
  -p 10909:10909 \
  -p 10911:10911 \
  -p 10912:10912 \
  -p 12888:12888 \
  --link rmqnamesrv:namesrv \
  apache/rocketmq:5.1.0 \
  sh mqbroker -c /opt/rocketmq/conf/broker.conf
```
