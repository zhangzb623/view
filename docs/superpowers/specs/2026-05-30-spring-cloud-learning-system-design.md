# Spring Cloud Learning System - Design Specification

**Project Name:** Spring Cloud Learning System
**Created:** 2026-05-30
**Version:** 1.0
**Author:** Zhang Zebiao

## Overview

A comprehensive Spring Cloud microservices learning platform demonstrating all 12 major technologies from the resume: Spring Cloud, MyBatis Plus, MySQL, Redis, ShardingJDBC, Kafka, RocketMQ, Netty, MongoDB, Docker, xxl-job, and Seata.

## Technology Stack

| Technology | Usage | Services |
|------------|-------|----------|
| **Spring Cloud** | Microservices architecture | All services |
| **MyBatis Plus** | ORM framework | All services |
| **MySQL** | Relational database | All services |
| **ShardingJDBC** | Database sharding | Order service |
| **Redis** | Caching | All services |
| **Redisson** | Distributed lock | All services |
| **Kafka** | Message queue | Order, Message services |
| **RocketMQ** | Message queue | Message, Order services |
| **Netty** | High-performance network | Chat server |
| **MongoDB** | NoSQL database | Admin, Scheduler services |
| **xxl-job** | Distributed task scheduling | Scheduler service |
| **Seata** | Distributed transaction | Distribution service |
| **Docker** | Containerization | All services |

## Architecture

### Project Structure

```
spring-cloud-learning-system/
├── docs/                           # Design documentation
│   ├── specs/                      # Technical specification docs
│   ├── database/                   # Database design docs
│   └── deployment/                 # Deployment documentation
├── common/                         # Common modules
│   ├── common-api/                 # Common API definitions
│   ├── common-starter/             # Common starters
│   ├── common-util/                # Common utilities
│   └── common-domain/              # Domain models
├── gateway/                        # Gateway service
│   ├── gateway-service/            # Spring Cloud Gateway
│   └── gateway-auth/               # Authentication service
├── user-service/                   # User service
├── product-service/                # Product service
├── order-service/                  # Order service
├── message-service/                # Message service
├── payment-service/                # Payment service
├── distribution-service/           # Distribution service (Seata)
├── scheduler-service/              # Scheduler service
├── admin-service/                  # Admin service
├── chat-server/                    # Chat server (Netty)
├── scripts/                        # Scripts
│   ├── docker-compose.yml          # Docker Compose orchestration
│   ├── mysql/                      # MySQL initialization scripts
│   ├── redis/                      # Redis configurations
│   ├── kafka/                      # Kafka configurations
│   ├── rocketmq/                   # RocketMQ configurations
│   ├── mongodb/                    # MongoDB configurations
│   ├── seata/                      # Seata configurations
│   └── xxl-job/                    # xxl-job configurations
├── docker/                         # Docker configurations
│   ├── base/                       # Base images
│   ├── mysql/
│   ├── redis/
│   ├── kafka/
│   ├── rocketmq/
│   ├── nacos/                      # Registration center
│   ├── seata/                      # Distributed transaction
│   ├── xxl-job/                    # Task scheduling
│   └── nginx/                      # Reverse proxy
└── pom.xml                         # Parent POM
```

### Service Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                    Service Layer Architecture                 │
└──────────────────────────────────────────────────────────────┘

Client Layer
    ↓
┌──────────────────────────────────────────────────────────────┐
│                    Spring Cloud Gateway (8080)                │
│                   (Unified entry + routing)                  │
└──────────────────────────────────────────────────────────────┘
    ↓
┌──────────────────────────────────────────────────────────────┐
│                    Service Mesh                               │
├──────────┬──────────┬──────────┬──────────┬─────────────────┤
│ Gateway  │  Auth    │  User    │  Product │   Order         │
│ Service  │ Service  │ Service  │ Service  │ Service         │
│ (8080)   │ (8081)   │ (8082)   │ (8083)   │ (8084)          │
├──────────┼──────────┼──────────┼──────────┼─────────────────┤
│Message   │  Payment │Distribution│ Scheduler│   Admin         │
│ Service  │ Service  │ Service  │ Service  │  Service        │
│ (8085)   │ (8086)   │ (8087)   │ (8088)   │  (8089)         │
└──────────┴──────────┴──────────┴──────────┴─────────────────┘
    ↓
┌──────────────────────────────────────────────────────────────┐
│                    Data Layer                                 │
├──────────────┬──────────────┬──────────────┬─────────────────┤
│   MySQL      │   Redis      │  MongoDB     │   Message Queues│
│   Cluster    │   Cluster    │   Cluster    │   (Kafka, RMQ)  │
└──────────────┴──────────────┴──────────────┴─────────────────┘
```

### Service Responsibilities

| Service | Port | Primary Responsibility | Key Technologies |
|---------|------|------------------------|------------------|
| Gateway | 8080 | Unified entry, routing, authentication, rate limiting | Spring Cloud Gateway, Spring Security |
| Auth | 8081 | User login, registration, token management | Spring Security, JWT, Redis |
| User | 8082 | User management, address, balance | MyBatis Plus, Redis |
| Product | 8083 | Product management, category, inventory | MyBatis Plus, ElasticSearch |
| Order | 8084 | Order creation, payment, shipping, refund | ShardingJDBC, Kafka, RocketMQ |
| Message | 8085 | Message push, notification, chat history | Kafka, RocketMQ, Redis |
| Payment | 8086 | Payment interface, callback, reconciliation | MyBatis Plus, ShardingJDBC |
| Distribution | 8087 | Commission calculation, settlement | Seata |
| Scheduler | 8088 | Task scheduling, data sync, reporting | xxl-job, MongoDB |
| Admin | 8089 | Admin management, logs, statistics | MyBatis Plus, MongoDB, Redis |
| Chat | 8090 | IM chat, group chat, message push | Netty, Redis |

## Database Design

### MySQL Database Schema

#### 1. User Database (`user_db.t_user_info`)

```sql
CREATE TABLE t_user_info (
    user_id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username         VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password         VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    phone            VARCHAR(20) COMMENT '手机号',
    email            VARCHAR(100) COMMENT '邮箱',
    avatar           VARCHAR(255) COMMENT '头像URL',
    balance          DECIMAL(10,2) DEFAULT 0.00 COMMENT '账户余额',
    status           TINYINT     DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    INDEX idx_username (username),
    INDEX idx_phone (phone)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';
```

#### 2. Order Table with Sharding (`order_db.t_order_0` - and `t_order_1`, `t_order_2`, `t_order_3`)

```sql
CREATE TABLE t_order (
    order_id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    order_no         VARCHAR(32)  NOT NULL UNIQUE COMMENT '订单号',
    user_id          BIGINT      NOT NULL COMMENT '用户ID(分片键)',
    product_id       BIGINT      NOT NULL COMMENT '商品ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_image    VARCHAR(255) COMMENT '商品图片',
    quantity         INT         NOT NULL DEFAULT 1 COMMENT '购买数量',
    unit_price       DECIMAL(10,2) NOT NULL COMMENT '单价',
    total_price      DECIMAL(10,2) NOT NULL COMMENT '总金额',
    status           TINYINT     DEFAULT 0 COMMENT '状态: 0待支付 1待发货 2待收货 3已完成 4已取消',
    pay_status       TINYINT     DEFAULT 0 COMMENT '支付状态: 0未支付 1已支付 2已退款',
    pay_time         DATETIME    COMMENT '支付时间',
    address_id       BIGINT      COMMENT '收货地址ID',
    shipping_fee     DECIMAL(10,2) DEFAULT 0.00 COMMENT '运费',
    remark           VARCHAR(500) COMMENT '备注',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (order_id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表(分片表)';
```

#### 3. Product Table (`product_db.t_product`)

```sql
CREATE TABLE t_product (
    product_id       BIGINT      NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    category_id      INT         NOT NULL COMMENT '分类ID',
    product_name     VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_desc     TEXT COMMENT '商品描述',
    product_image    VARCHAR(255) COMMENT '商品图片',
    unit_price       DECIMAL(10,2) NOT NULL COMMENT '单价',
    stock            INT         NOT NULL DEFAULT 0 COMMENT '库存',
    status           TINYINT     DEFAULT 1 COMMENT '状态: 0下架 1上架 2删除',
    sales_count      INT         DEFAULT 0 COMMENT '销量',
    create_time      DATETIME    DEFAULT CURRENT_TIMESTAMP,
    update_time      DATETIME    DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (product_id),
    KEY idx_category (category_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';
```

### Sharding Strategy (Order Service)

**Sharding Algorithm:** Hash Modulo
- **Sharding Count:** 4
- **Sharding Key:** `user_id`
- **Routing Rule:** `sharding_id = user_id % 4`

**ShardingJDBC Configuration:**

```yaml
spring:
  shardingsphere:
    datasource:
      names: order0,order1,order2,order3
    sharding:
      tables:
        t_order:
          actual-data-nodes: order${0..3}.t_order
          table-strategy:
            standard:
              sharding-column: user_id
              sharding-algorithm-name: order-hash-mod
      sharding-algorithms:
        order-hash-mod:
          type: HASH_MOD
          props:
            sharding-count: 4
```

### Redis Key Design

```
cache:product:{product_id}                  # 商品详情缓存 (TTL 3600s)
cache:user:{user_id}                        # 用户信息缓存 (TTL 1800s)
cache:order:wait_pay:{user_id}:{order_no}   # 待支付订单 (TTL 30m)
cache:order:{order_id}                      # 订单详情缓存 (TTL 7200s)
cache:lock:create_order                     # 分布式锁 (30s TTL)
cache:lock:pay:{order_no}                   # 支付锁 (300s TTL)
cache:session:{token}                       # 用户会话 (TTL 2h)
cache:limit:api:{user_id}:{api}             # 接口限流 (TTL 60s)
```

### MongoDB Collections

```javascript
// 操作日志
db.t_operation_log.insertOne({
    user_id: 12345,
    action: "CREATE_ORDER",
    module: "order",
    ip: "192.168.1.100",
    params: { order_no: "ORD2024001" },
    create_time: ISODate("2024-05-29T10:00:00Z")
});

// 支付日志
db.t_payment_log.insertOne({
    payment_no: "PAY202405290001",
    order_id: 10001,
    amount: 99.00,
    payment_method: "ALIPAY",
    status: "SUCCESS",
    create_time: ISODate("2024-05-29T10:05:00Z")
});

// 统计数据
db.t_statistics.updateOne(
    { date: "2024-05-29" },
    {
        $inc: {
            total_orders: 1,
            total_sales: 99.00
        },
        $set: { update_time: ISODate() }
    },
    { upsert: true }
);
```

## Message Queue Architecture

### Queue Selection Strategy

| Technology | Usage | Queue Name | Message Type |
|------------|-------|------------|--------------|
| **Kafka** | Order async processing | order.created | Order created event |
| | | order.paid | Order paid event |
| | | order.canceled | Order canceled event |
| **RocketMQ** | Message push | message.send | Message send |
| | | message.notify | Notification push |
| | | order.delay | Delay message (timeout cancel) |

### Message Schema

#### 1. Order Created Event (Kafka)

```json
{
  "orderId": 10001,
  "orderNo": "ORD202405290001",
  "userId": 12345,
  "productId": 1001,
  "productName": "iPhone 15",
  "quantity": 1,
  "totalPrice": 5999.00,
  "createTime": "2024-05-29T10:00:00Z"
}
```

#### 2. Order Paid Event (Kafka)

```json
{
  "orderId": 10001,
  "orderNo": "ORD202405290001",
  "userId": 12345,
  "paymentNo": "PAY202405290001",
  "amount": 5999.00,
  "payTime": "2024-05-29T10:05:00Z",
  "paymentMethod": "ALIPAY"
}
```

#### 3. Message Notification (RocketMQ)

```json
{
  "messageId": "MSG202405290001",
  "type": "CHAT_MESSAGE",
  "fromUserId": 12345,
  "toUserId": 67890,
  "content": {
    "text": "您好，您的订单已发货",
    "orderId": 10001
  },
  "priority": 1,
  "create_time": "2024-05-29T10:10:00Z"
}
```

## Distributed Transaction (Seata)

### Seata Architecture Overview

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│ Order Service│      │Inventory    │      │Payment      │
│   (TM)      │      │   Service   │      │   Service   │
│             │      │   (RM)      │      │   (RM)      │
└──────┬──────┘      └──────┬──────┘      └──────┬──────┘
       │                    │                    │
       ├─────────────────────────────────────────┤
       │  注册全局事务 (XID)                       │
       ▼                                        ▼
┌──────────────────────────────────────────────────────────────────────┐
│                        Seata Server (AT模式)                         │
│                       正常：提交事务  异常：回滚事务                  │
└──────────────────────────────────────────────────────────────────────┘
       │                    │                    │
       │  记录全局锁        │                    │
       ▼                    ▼                    ▼
  Inventory Service    Payment Service       Order Service
  (Update stock)       (Deduct balance)     (Create order)
```

### Scenario: Create Order + Deduct Inventory + Deduct Balance

```java
@TransactionalPropagation(propagation = Propagation.REQUIRES_NEW)
public Long createOrder(CreateOrderRequest request) {
    // 1. 开启全局事务 (Seata会自动处理)
    GlobalTransaction tx = globalTransactionManager.begin();

    try {
        // 2. 扣减库存
        inventoryServiceClient.deductStock(request.getProductId(), request.getQuantity());

        // 3. 扣减余额
        userServiceClient.deductBalance(request.getUserId(), request.getTotalPrice());

        // 4. 创建订单
        OrderDO order = new OrderDO();
        order.setOrderNo(generateOrderNo());
        order.setUserId(request.getUserId());
        order.setProductId(request.getProductId());
        order.setQuantity(request.getQuantity());
        order.setTotalPrice(request.getTotalPrice());
        orderMapper.insert(order);

        // 5. 提交全局事务
        globalTransactionManager.commit();
        return order.getOrderId();

    } catch (Exception e) {
        // 6. 回滚全局事务
        globalTransactionManager.rollback();
        throw e;
    }
}
```

## Netty Chat Server Architecture

### Message Protocol Design

**Binary Protocol Structure:**

```
┌─────────────────────────────────────┐
│ Magic: 4 bytes  (固定: "CHAT")        │
│ Version: 1 byte                        │
│ Command: 1 byte                        │
│ Length: 2 bytes                        │
│ Content: N bytes                       │
└─────────────────────────────────────┘
```

**Command Types:**
- `LOGIN(0x01)` - 登录
- `LOGOUT(0x02)` - 登出
- `CHAT(0x03)` - 单聊
- `GROUP_CHAT(0x04)` - 群聊
- `SYSTEM(0x05)` - 系统消息
- `PING(0x06)` - 心跳
- `PONG(0x07)` - 心跳响应

**Example Message:**

```java
{
  "magic": "CHAT",
  "version": 1,
  "command": "CHAT",
  "content": {
    "fromUserId": 12345,
    "toUserId": 67890,
    "content": "Hello!",
    "timestamp": 1717065600000
  }
}
```

## Distributed Task Scheduling (xxl-job)

### Scheduled Tasks

**1. Data Sync Task** - Every 5 minutes, sharded execution
- Sync orders to MongoDB
- Sync users to MongoDB
- Clean old data

**2. Order Statistics Task** - Every hour
- Calculate daily statistics
- Save to MongoDB

**3. Clean Expire Data Task** - Daily at 2 AM
- Clean 7-day old orders
- Clean 30-day old messages

**4. Order Timeout Check Task** - Every 5 minutes
- Check unpaid orders
- Auto-cancel timeout orders

### xxl-job Configuration

```yaml
xxl:
  job:
    admin:
      addresses: http://localhost:8088/xxl-job-admin
    executor:
      appname: learning-system-scheduler
      port: 9999
      logpath: /data/applogs/xxl-job/jobhandler
      logretentiondays: 30
```

## Docker & Deployment

### Docker Compose Services

| Service | Port | Technology |
|---------|------|------------|
| nacos | 8848 | Service Registry |
| seata | 8091 | Distributed Transaction |
| zookeeper | 2181 | Kafka dependency |
| kafka | 9092 | Message Queue |
| rocketmq-namesrv | 9876 | RocketMQ Nameserver |
| rocketmq-broker | 10909, 10911 | RocketMQ Broker |
| mysql | 3306 | Database |
| redis | 6379 | Cache |
| rabbitmq | 5672, 15672 | Message Queue |
| mongodb | 27017 | NoSQL Database |
| xxl-job-admin | 8088 | Task Scheduling |
| gateway-service | 8080 | Gateway |
| user-service | 8082 | User Service |
| product-service | 8083 | Product Service |
| order-service | 8084 | Order Service |
| payment-service | 8086 | Payment Service |
| scheduler-service | 8088 | Scheduler Service |
| chat-server | 8090 | Netty Chat Server |

### Quick Deploy Script

```bash
#!/bin/bash
./scripts/deploy.sh
```

### Access URLs

- Gateway: http://localhost:8080
- Nacos: http://localhost:8848/nacos
- xxl-job: http://localhost:8088/xxl-job-admin
- RocketMQ Admin: http://localhost:15672
- RabbitMQ: http://localhost:15672

## Implementation Checklist

### Phase 1: Basic Infrastructure
- [ ] Project structure & basic POM
- [ ] Common module (util, domain, starter)
- [ ] Nacos registration center
- [ ] Gateway service
- [ ] MySQL initialization

### Phase 2: Core Services
- [ ] User service (MySQL + Redis)
- [ ] Product service (MySQL + Redis + ElasticSearch)
- [ ] Order service (MySQL + ShardingJDBC + Kafka + RocketMQ)
- [ ] Payment service (MySQL + Redis + ShardingJDBC)

### Phase 3: Advanced Features
- [ ] Seata distributed transaction
- [ ] Message service (Kafka + RocketMQ)
- [ ] Scheduler service (xxl-job + MongoDB)
- [ ] Chat server (Netty)
- [ ] Distribution service

### Phase 4: Docker & Deployment
- [ ] Dockerfiles for all services
- [ ] Docker Compose orchestration
- [ ] Deployment scripts
- [ ] Health checks & monitoring

## Testing Strategy

**Unit Tests:**
- Service layer tests
- Business logic tests
- Edge case tests

**Integration Tests:**
- Service-to-service calls
- Database operations
- Message queue consumption

**Performance Tests:**
- Order creation throughput
- Concurrent order queries
- Chat message latency

**Chaos Engineering:**
- Service outage simulation
- Database failure recovery
- Message queue failures

## Conclusion

This design provides a comprehensive learning platform covering all 12 major technologies from the resume. The architecture follows Spring Cloud best practices and simulates production scenarios with proper separation of concerns, fault tolerance, and scalability considerations.
