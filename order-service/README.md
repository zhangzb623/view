# Order Service

订单服务，演示Spring Cloud微服务、ShardingJDBC分片、Kafka消息队列、RocketMQ消息队列等核心技术。

## 功能列表

### 1. 订单管理
- ✅ 创建订单（Feign调用商品服务、用户服务）
- ✅ 支付订单
- ✅ 取消订单（分布式锁 + 退款 + 恢复库存）
- ✅ 根据订单ID查询订单
- ✅ 根据用户ID查询订单列表（分页）
- ✅ 更新订单状态
- ✅ 订单发货
- ✅ 订单完成

### 2. 数据分片（ShardingJDBC）
- ✅ 用户ID分片（user_id % 4）
- ✅ 4个数据源（ds0-ds3）
- ✅ 16个分片表（t_order_0 - t_order_3, 每个分片4个表）
- ✅ 自动路由到对应分片

### 3. 消息队列
- ✅ Kafka消息生产者
  - 订单创建事件
  - 订单支付事件
  - 订单取消事件
  - 订单完成事件
- ✅ RocketMQ消息生产者
  - 订单延迟取消（30分钟超时）
  - 订单支付成功通知
  - 订单发货通知

### 4. 服务调用
- ✅ Feign调用商品服务
  - 检查商品是否在售
  - 扣减商品库存
  - 增加商品库存
- ✅ Feign调用用户服务
  - 获取用户信息
  - 扣减用户余额
  - 增加用户余额（退款）
  - 根据用户名查询用户

### 5. 分布式锁
- ✅ Redis分布式锁防止重复支付
- ✅ Redis分布式锁防止重复取消

## 技术实现

### 依赖技术
- **Spring Boot 3.2.0**: 基础框架
- **Spring Cloud**: 微服务架构
- **MyBatis Plus 3.5.5**: ORM框架
- **ShardingJDBC 5.4.1**: 数据库分片
- **Spring Cloud OpenFeign**: 服务调用
- **Kafka 3.5.1**: 消息队列
- **RocketMQ 5.1.0**: 消息队列
- **Common Domain**: 基础实体类
- **Common Starter**: 通用组件

### 核心功能实现

#### 1. 订单创建流程
```
用户创建订单
  → Feign调用商品服务（检查商品、扣减库存）
    → Feign调用用户服务（扣减余额）
      → 插入订单到数据库（ShardingJDBC自动分片）
        → 发送Kafka事件
        → 发送RocketMQ延迟取消消息（30分钟）
```

#### 2. 订单支付流程
```
用户支付订单
  → 分布式锁（防止重复支付）
    → 更新订单状态（待发货）
    → 发送Kafka事件（订单支付）
    → 发送RocketMQ通知（支付成功）
```

#### 3. 订单取消流程
```
用户取消订单
  → 分布式锁（防止重复取消）
    → 检查订单状态（只有待支付可取消）
      → 更新订单状态（已取消）
      → Feign调用用户服务（增加余额退款）
      → Feign调用商品服务（恢复库存）
        → 发送Kafka事件（订单取消）
        → 发送RocketMQ通知（取消成功）
```

## API接口

### 基础路径
`http://localhost:8084/api/order`

### 接口列表

#### 创建订单
```
POST /create
Content-Type: application/json

{
  "productId": 1,
  "productName": "iPhone 15 Pro",
  "quantity": 1,
  "unitPrice": 7999.00,
  "totalPrice": 7999.00,
  "paymentMethod": 3,
  "address": "广东省深圳市南山区",
  "receiver": "张三",
  "receiverPhone": "13800138000"
}
```

#### 支付订单
```
POST /{orderId}/pay?transactionId=TXN123&paymentMethod=3
```

#### 取消订单
```
POST /cancel
Content-Type: application/json

{
  "orderId": 1,
  "cancelReason": "不想要了"
}
```

#### 查询订单
```
GET /{orderId}
```

#### 查询订单列表
```
GET /user/list?userId=1&status=0&page=1&size=10
```

#### 更新订单状态
```
PUT /{orderId}/status?status=1
```

#### 订单发货
```
POST /{orderId}/ship?trackingNumber=SF123456789
```

#### 订单完成
```
POST /{orderId}/complete
```

## 架构设计

### 服务架构
```
Order Service
├── Controller (OrderController) - 处理HTTP请求
├── Service (OrderService) - 业务逻辑
├── Mapper (OrderMapper) - 数据访问
├── Entity (OrderDO) - 数据库实体
├── DTO (CreateOrderRequest, OrderDTO, etc.) - 数据传输对象
├── Feign Client (ProductFeignClient, UserFeignClient) - 服务调用
├── Producer (OrderKafkaProducer, OrderRocketMQProducer) - 消息生产
├── Event (OrderEvent) - 消息事件
└── Repository - ShardingJDBC配置
```

### ShardingJDBC分片策略

```
订单表结构: t_order (逻辑表)
  分片键: user_id
  分片算法: Modulo (user_id % 4)

数据源配置:
  ds0, ds1, ds2, ds3

分片路由:
  user_id=1 → ds0.t_order_1
  user_id=2 → ds1.t_order_2
  user_id=3 → ds2.t_order_3
  user_id=4 → ds3.t_order_0
  user_id=5 → ds0.t_order_1
  ...

分片表数量: 16个（ds0-ds3 × t_order_0-t_order_3）
```

### 数据库表

### t_order (订单表) - 分片表
| 字段 | 类型 | 说明 |
|------|------|------|
| order_id | BIGINT | 订单ID（主键，自增） |
| user_id | BIGINT | 用户ID（分片键） |
| product_id | BIGINT | 商品ID |
| product_name | VARCHAR(200) | 商品名称 |
| quantity | INT | 商品数量 |
| unit_price | DECIMAL(10,2) | 单价 |
| total_price | DECIMAL(10,2) | 总金额 |
| status | TINYINT | 状态（0待支付 1待发货 2待收货 3已完成 4已取消） |
| payment_method | TINYINT | 支付方式（1支付宝 2微信 3余额） |
| payment_status | TINYINT | 支付状态（0未支付 1已支付） |
| transaction_id | VARCHAR(100) | 交易流水号 |
| remark | VARCHAR(500) | 备注 |
| address | VARCHAR(500) | 收货地址 |
| receiver | VARCHAR(50) | 收货人 |
| receiver_phone | VARCHAR(20) | 收货电话 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 删除标记 |

### 消息队列

#### Kafka Topic
- `order.created` - 订单创建事件
- `order.paid` - 订单支付事件
- `order.cancelled` - 订单取消事件
- `order.completed` - 订单完成事件

#### RocketMQ Topic
- `order-cancel-delay-topic` - 订单延迟取消（DELAY_TIME_LEVEL=4，30分钟）
- `order-cancel-topic` - 订单取消通知
- `order-payment-topic` - 订单支付成功通知
- `order-ship-topic` - 订单发货通知

### 数据流

#### 1. 订单创建
```
Controller → Service (createOrder)
  → Feign调用ProductService.checkProduct (检查商品)
  → Feign调用ProductService.deductStock (扣减库存)
  → Feign调用UserService.deductBalance (扣减余额)
  → OrderMapper.insert (插入订单)
  → KafkaProducer.sendOrderCreatedEvent (发送Kafka事件)
  → RocketMQProducer.sendOrderCancelDelayMessage (发送延迟取消消息)
```

#### 2. 订单支付
```
Controller → Service (payOrder)
  → LockHelper.executeWithLock (分布式锁)
    → OrderMapper.updateById (更新订单状态)
    → KafkaProducer.sendOrderPaidEvent (发送Kafka事件)
    → RocketMQProducer.sendOrderPaymentSuccess (发送通知)
```

#### 3. 订单取消
```
Controller → Service (cancelOrder)
  → LockHelper.executeWithLock (分布式锁)
    → OrderMapper.selectById (查询订单)
    → OrderMapper.updateById (更新订单状态)
    → Feign调用UserService.addBalance (退款)
    → Feign调用ProductService.addStock (恢复库存)
    → KafkaProducer.sendOrderCancelledEvent (发送Kafka事件)
    → RocketMQProducer (发送通知)
```

## 使用示例

### 创建并支付订单
```bash
# 1. 创建订单
curl -X POST http://localhost:8084/api/order/create \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "productId": 1,
    "productName": "iPhone 15 Pro",
    "quantity": 1,
    "unitPrice": 7999.00,
    "totalPrice": 7999.00,
    "paymentMethod": 3,
    "address": "广东省深圳市南山区",
    "receiver": "张三",
    "receiverPhone": "13800138000"
  }'

# 返回: {"code": 200, "message": "订单创建成功", "data": 1}

# 2. 支付订单
curl -X POST "http://localhost:8084/api/order/1/pay?transactionId=TXN123&paymentMethod=3"

# 返回: {"code": 200, "message": "支付成功"}
```

### 查询订单
```bash
curl -X GET http://localhost:8084/api/order/1
```

### 取消订单
```bash
curl -X POST http://localhost:8084/api/order/cancel \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "cancelReason": "不想要了"
  }'
```

### 查询订单列表
```bash
curl -X GET "http://localhost:8084/api/order/user/list?userId=1&status=0&page=1&size=10"
```

### 订单发货
```bash
curl -X POST "http://localhost:8084/api/order/1/ship?trackingNumber=SF123456789"
```

### 订单完成
```bash
curl -X POST http://localhost:8084/api/order/1/complete
```

## 注意事项

1. **分片键一致性**: 使用user_id作为分片键，所有订单操作必须通过userId路由到正确的分片
2. **分布式锁**: 支付和取消操作使用分布式锁防止并发问题
3. **消息可靠性**: Kafka和RocketMQ消息发送使用同步发送，确保消息不丢失
4. **事务一致性**: Feign调用失败时需要回滚（如余额扣减失败时恢复库存）
5. **延迟消息**: 使用RocketMQ延迟消息实现30分钟超时自动取消
6. **状态机**: 订单状态流转符合业务规则（0待支付 → 1待发货 → 2待收货 → 3已完成）

## 扩展功能

- [ ] 订单评价
- [ ] 订单导出
- [ ] 订单统计
- [ ] 订单搜索（按商品名、订单号）
- [ ] 订单批量操作
- [ ] 订单退款流程

## 相关技术文档

- [Spring Cloud文档](https://spring.io/projects/spring-cloud)
- [MyBatis Plus文档](https://baomidou.com/)
- [ShardingJDBC文档](https://shardingsphere.apache.org/document/current/en/overview/)
- [Kafka文档](https://kafka.apache.org/documentation/)
- [RocketMQ文档](https://rocketmq.apache.org/docs/)

## 与其他服务的交互

### User Service
- Feign客户端: `UserFeignClient`
- 调用方法: `getUserInfo`, `deductBalance`, `addBalance`, `getUserByUsername`

### Product Service
- Feign客户端: `ProductFeignClient`
- 调用方法: `getProductById`, `deductStock`, `addStock`, `isProductOnSale`

### Nacos注册中心
- 服务名称: `order-service`
- 命名空间: `learning-system`
- 分组: `LEARNING_GROUP`

## 配置说明

### Kafka配置
```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: StringSerializer
      value-serializer: JsonSerializer
```

### RocketMQ配置
```yaml
spring:
  rocketmq:
    name-server: localhost:9876
    producer:
      group: order-producer-group
```

### ShardingJDBC配置
```yaml
spring:
  shardingsphere:
    rules:
      sharding:
        tables:
          t_order:
            actual-data-nodes: ds$->{0..3}.t_order_$->{0..3}
            table-strategy:
              standard:
                sharding-column: user_id
                sharding-algorithm-name: user-id-mod
        sharding-algorithms:
          user-id-mod:
            type: ModuloShardingAlgorithm
            props:
              algorithm-expression: t_order_$->{user_id % 4}
```
