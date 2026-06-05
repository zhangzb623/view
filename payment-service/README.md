# Payment Service

支付服务，演示Seata分布式事务、MyBatis Plus、Redis、Redisson等核心技术。

## 功能列表

### 1. 支付管理
- ✅ 创建支付记录
- ✅ 调用第三方支付（模拟）
- ✅ 根据ID查询支付
- ✅ 根据订单ID查询支付
- ✅ 查询用户支付记录
- ✅ 查询未支付支付记录

### 2. 退款管理
- ✅ 创建退款记录
- ✅ 调用第三方退款（模拟）
- ✅ 根据ID查询退款
- ✅ 根据订单ID查询退款
- ✅ 查询订单退款记录

### 3. Seata分布式事务
- ✅ AT模式（自动事务）
- ✅ TCC模式（手动事务）
- ✅ Saga模式（长事务）

### 4. ShardingJDBC分片
- ✅ 用户ID分片（user_id % 4）
- ✅ 4个分片表（t_payment_0 - t_payment_3）

### 5. Redis缓存
- ✅ 支付记录缓存
- ✅ 缓存过期时间1小时

### 6. Feign服务调用
- ✅ 调用Order Service
- ✅ 查询订单信息
- ✅ 支付订单
- ✅ 取消订单

## 技术实现

### 依赖技术
- **Spring Boot 3.2.0**: 基础框架
- **Spring Cloud**: 微服务架构
- **MyBatis Plus 3.5.5**: ORM框架
- **ShardingJDBC 5.4.1**: 数据库分片
- **Seata 1.7.1**: 分布式事务
- **Redisson**: 分布式锁
- **Spring Cloud OpenFeign**: 服务调用
- **Common Domain**: 基础实体类
- **Common Starter**: 通用组件

### 核心功能实现

#### 1. 创建支付（AT模式分布式事务）
```
用户创建支付
  → 检查订单是否存在
  → 创建支付记录
  → 缓存支付记录
```

#### 2. 第三方支付（AT模式）
```
支付服务调用第三方支付
  → AT模式自动管理事务
  → 更新支付状态
  → 通知Order Service
    → Order Service更新订单状态
  → 分布式事务自动提交
```

#### 3. 创建退款（TCC模式）
```
用户申请退款
  → 检查支付记录
  → 创建退款记录
    → TCC阶段：Try
      → 冻结支付金额
    → Saga模式处理退款流程
```

## API接口

### 基础路径
`http://localhost:8085/api/payment`

### 支付接口

#### 创建支付
```
POST /create
Content-Type: application/json

{
  "orderId": 1,
  "userId": 1,
  "paymentMethod": 3,
  "amount": 7999.00,
  "refundReason": ""
}
```

#### 调用第三方支付
```
POST /{paymentId}/pay
```

#### 查询支付详情
```
GET /{paymentId}
```

#### 根据订单ID查询支付
```
GET /order/{orderId}
```

#### 查询用户支付记录
```
GET /user/payments?userId=1&page=1&size=10
```

#### 查询未支付支付记录
```
GET /user/unpaid?userId=1
```

### 退款接口

#### 创建退款
```
POST /refund/create
Content-Type: application/json

{
  "paymentId": 1,
  "refundAmount": 7999.00,
  "refundReason": "不想要了"
}
```

#### 调用第三方退款
```
POST /refund/{refundId}/refund
```

#### 查询退款详情
```
GET /refund/{refundId}
```

#### 根据订单ID查询退款
```
GET /refund/order/{orderId}
```

#### 查询订单退款记录
```
GET /refund/order/{orderId}/all
```

## 架构设计

### 服务架构
```
Payment Service
├── Controller (PaymentController, RefundController) - 处理HTTP请求
├── Service (PaymentService, RefundService) - 业务逻辑
├── Mapper (PaymentMapper, RefundMapper) - 数据访问
├── Entity (PaymentDO, RefundDO) - 数据库实体
├── DTO (CreatePaymentRequest, PaymentDTO, RefundDTO, etc.) - 数据传输对象
├── Feign Client (OrderFeignClient) - 服务调用
└── Config (SeataConfig, RedissonConfig) - 分布式事务配置
```

### Seata分布式事务架构

#### AT模式（自动事务）
```
Seata AT模式
  ┌─────────────────┐
  │   Transaction   │
  │     Manager     │
  └────────┬────────┘
           │
  ┌────────▼────────┐
  │  Global Undo   │  (自动记录UNDO_LOG)
  │     Log         │
  └────────┬────────┘
           │
  ┌────────▼────────┐
  │   Branch Undo   │  (分支事务UNDO)
  │     Log         │
  └────────┬────────┘
           │
  ┌────────▼────────┐
  │   Payment       │   ┌──────────────┐
  │   Order Service │──→│ Third Party  │
  │                 │   │   Payment    │
  └─────────────────┘   └──────────────┘
```

#### TCC模式（手动事务）
```
Seata TCC模式
  ┌─────────────────┐
  │   Transaction   │
  │     Manager     │
  └────────┬────────┘
           │
  ┌────────▼────────┐
  │   Try Phase     │  (冻结金额)
  └────────┬────────┘
           │
  ┌────────▼────────┐
  │   Confirm Phase │  (确认支付)
  └────────┬────────┘
           │
  ┌────────▼────────┐
  │   Cancel Phase  │  (取消支付)
  └─────────────────┘
```

#### Saga模式（长事务）
```
Saga模式
  ┌─────────────────┐
  │  Transaction    │
  │     Manager     │
  └────────┬────────┘
           │
  ┌────────▼────────┐
  │  Sub-transaction│
  │    Sequences    │
  └─────────────────┘
           │
  ┌────────▼────────┐
  │  Update Payment │  ┌──────────────┐
  │  Update Order   │──→│ Refund Step  │
  │  Notify User    │   └──────────────┘
  └─────────────────┘
```

### 数据库表

### t_payment (支付表) - 分片表
| 字段 | 类型 | 说明 |
|------|------|------|
| payment_id | BIGINT | 支付ID（主键，自增） |
| user_id | BIGINT | 用户ID（分片键） |
| order_id | BIGINT | 订单ID |
| payment_method | TINYINT | 支付方式（1支付宝 2微信 3余额） |
| amount | DECIMAL(10,2) | 支付金额 |
| status | TINYINT | 支付状态（0未支付 1已支付 2处理中） |
| transaction_id | VARCHAR(100) | 交易流水号 |
| channel_code | VARCHAR(50) | 渠道返回码 |
| channel_message | VARCHAR(500) | 渠道返回消息 |
| refund_amount | DECIMAL(10,2) | 退款金额 |
| refund_status | TINYINT | 退款状态（0未退款 1退款中 2已退款） |
| refund_reason | VARCHAR(500) | 退款原因 |
| remark | VARCHAR(500) | 备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 删除标记 |

### t_refund (退款表)
| 字段 | 类型 | 说明 |
|------|------|------|
| refund_id | BIGINT | 退款ID（主键，自增） |
| user_id | BIGINT | 用户ID |
| order_id | BIGINT | 订单ID |
| payment_id | BIGINT | 支付记录ID |
| refund_amount | DECIMAL(10,2) | 退款金额 |
| status | TINYINT | 退款状态（0待处理 1处理中 2成功 3失败） |
| refund_reason | VARCHAR(500) | 退款原因 |
| apply_time | DATETIME | 退款申请时间 |
| complete_time | DATETIME | 退款完成时间 |
| refund_channel | VARCHAR(50) | 退款渠道 |
| refund_transaction_id | VARCHAR(100) | 退款渠道流水号 |
| remark | VARCHAR(500) | 备注 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |
| deleted | TINYINT | 删除标记 |

### Seata配置

#### AT模式配置
```yaml
seata:
  enabled: true
  application-id: payment-service
  tx-service-group: payment-service-group
  registry:
    type: nacos
    nacos:
      server-addr: localhost:8848
      namespace: learning-system
      group: SEATA_GROUP
  config:
    type: nacos
    nacos:
      server-addr: localhost:8848
      namespace: learning-system
      group: SEATA_GROUP
```

#### 分布式事务配置
```yaml
# 订单服务 - AT模式
order-service:
  seata:
    tx-service-group: order-service-group

# 支付服务 - AT模式
payment-service:
  seata:
    tx-service-group: payment-service-group

# 分布式事务协调者
# 需要部署Seata Server
```

## 使用示例

### 创建并支付订单
```bash
# 1. 创建支付
curl -X POST http://localhost:8085/api/payment/create \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "userId": 1,
    "paymentMethod": 3,
    "amount": 7999.00
  }'

# 返回: {"code": 200, "message": "支付记录创建成功", "data": 1}

# 2. 调用第三方支付
curl -X POST http://localhost:8085/api/payment/1/pay

# 返回: {"code": 200, "message": "第三方支付调用成功"}
```

### 查询支付
```bash
curl -X GET http://localhost:8085/api/payment/1
```

### 创建退款
```bash
curl -X POST http://localhost:8085/api/payment/refund/create \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": 1,
    "refundAmount": 7999.00,
    "refundReason": "不想要了"
  }'
```

### 查询退款
```bash
# 查询单个退款
curl -X GET http://localhost:8085/api/payment/refund/1

# 根据订单查询退款
curl -X GET http://localhost:8085/api/payment/refund/order/1

# 查询订单所有退款
curl -X GET http://localhost:8085/api/payment/refund/order/1/all
```

## 注意事项

1. **Seata服务启动**: 需要先启动Seata Server（standalone模式或cluster模式）
2. **AT模式**: 自动记录UNDO_LOG，无需手动管理事务边界
3. **TCC模式**: 需要实现Try/Confirm/Cancel三个阶段
4. **Saga模式**: 适合长事务，需要自定义补偿逻辑
5. **分片键**: 使用user_id作为分片键，所有支付操作必须路由到正确的分片
6. **缓存一致性**: 支付状态更新时需要同步清除缓存
7. **幂等性**: 第三方支付/退款需要保证幂等性

## 扩展功能

- [ ] 支付宝/微信支付对接
- [ ] 支付宝/微信退款对接
- [ ] 支付宝/微信回调处理
- [ ] 支付统计
- [ ] 支付报表
- [ ] 对账功能

## 相关技术文档

- [Seata官方文档](https://seata.io/zh-cn/docs/overview/what-is-seata.html)
- [MyBatis Plus文档](https://baomidou.com/)
- [ShardingJDBC文档](https://shardingsphere.apache.org/document/current/en/overview/)
- [Spring Cloud文档](https://spring.io/projects/spring-cloud)
- [Redisson文档](https://github.com/redisson/redisson/wiki)

## 与其他服务的交互

### Order Service
- Feign客户端: `OrderFeignClient`
- 调用方法: `getOrderById`, `payOrder`, `cancelOrder`, `getOrdersByUserId`

### User Service
- 通过Feign调用

## 配置说明

### Seata AT模式
- `@Transactional`: 自动转换为Seata AT模式
- 自动记录UNDO_LOG
- 自动分支注册
- 自动提交/回滚

### Seata TCC模式
- `@TwoPhaseBusinessAction`: TCC模式注解
- Try: 预执行
- Confirm: 确认执行
- Cancel: 取消执行

### ShardingJDBC配置
```yaml
spring:
  shardingsphere:
    rules:
      sharding:
        tables:
          t_payment:
            actual-data-nodes: ds.t_payment_$->{0..3}
            table-strategy:
              standard:
                sharding-column: user_id
                sharding-algorithm-name: user-id-mod
        sharding-algorithms:
          user-id-mod:
            type: ModuloShardingAlgorithm
            props:
              algorithm-expression: t_payment_$->{user_id % 4}
```

## Seata部署

### Standalone模式（单机）
```bash
# 1. 下载Seata Server
wget https://github.com/apache/incubator-seata/releases/download/v1.7.1/seata-server-1.7.1.zip

# 2. 解压并启动
unzip seata-server-1.7.1.zip
cd seata-server-1.7.1/bin

# 3. 启动Seata Server
./seata-server.sh

# 4. 访问管理控制台
http://localhost:8090
```

### Docker部署
```bash
docker run -d \
  --name seata-server \
  -p 8091:8091 \
  -p 8092:8092 \
  -p 8093:8093 \
  -e SEATA_IP=localhost \
  seataio/seata-server:1.7.1
```

## 分布式事务使用示例

### AT模式示例
```java
@Transactional
public Long createPayment(CreatePaymentRequest request) {
    // 检查订单
    Result<Object> orderResult = orderFeignClient.getOrderById(request.getOrderId(), "mock-token");

    // 创建支付记录
    PaymentDO payment = new PaymentDO();
    payment.setOrderId(request.getOrderId());
    // ...

    paymentMapper.createPayment(payment);

    // Seata AT模式自动管理事务
    // 自动记录UNDO_LOG
    // 自动注册分支事务
    // 提交时自动提交分支
    // 回滚时自动回滚

    return payment.getPaymentId();
}
```

### TCC模式示例
```java
@TwoPhaseBusinessAction(name = "payment", commitMethod = "confirm", rollbackMethod = "cancel")
public boolean prepare(Long paymentId) {
    // Try阶段：冻结金额
    PaymentDO payment = paymentMapper.selectById(paymentId);
    payment.setStatus(2); // 冻结状态
    paymentMapper.updateById(payment);
    return true;
}

public boolean confirm(@BusinessActionContextParameter(paramName = "paymentId") Long paymentId) {
    // Confirm阶段：确认支付
    PaymentDO payment = paymentMapper.selectById(paymentId);
    payment.setStatus(1); // 已支付
    paymentMapper.updateById(payment);
    return true;
}

public boolean cancel(@BusinessActionContextParameter(paramName = "paymentId") Long paymentId) {
    // Cancel阶段：取消支付
    PaymentDO payment = paymentMapper.selectById(paymentId);
    payment.setStatus(0); // 未支付
    paymentMapper.updateById(payment);
    return true;
}
```
