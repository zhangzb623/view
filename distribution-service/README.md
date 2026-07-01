# Distribution Service

分发服务用于演示订单分销佣金的计算、落库、查询与结算，是当前项目中用于承接 **Seata 分布式事务** 演示的核心服务。

当前已经实现两步：
- 第一阶段：最小可用切片
  - 根据订单计算分销记录
  - 落库保存分销佣金记录
  - 按分销 ID / 订单 ID / 分销用户查询
- 第二阶段：Seata 结算链路
  - 手动结算分销记录
  - 调用 `user-service` 增加分销用户余额
  - 用 Seata 包裹本地状态更新与跨服务余额变更这条事务链路

---

## 当前能力

### 已完成
- Spring Boot 服务骨架
- MyBatis Plus 持久化
- Feign 调用 `order-service`
- Feign 调用 `user-service`
- 分销记录计算
- 分销记录查询
- 分销记录结算
- Seata 全局事务注解接入
- Nacos 配置接入
- 数据库初始化脚本

### 当前范围内不做
- 多级分销
- 推广树 / 邀请关系
- 自动结算任务
- 分销提现
- Kafka / RocketMQ 分销事件通知

---

## 服务端口

- `8090`

---

## 数据库

初始化脚本位置：
- `scripts/mysql/init.sql`

新增数据库：
- `distribution_db`

新增表：
- `t_distribution_record`

### 表结构含义

| 字段 | 说明 |
| --- | --- |
| `distribution_id` | 分销记录 ID |
| `order_id` | 订单 ID，当前一单只允许一条分销记录 |
| `order_user_id` | 下单用户 ID |
| `distributor_user_id` | 分销用户 ID |
| `product_id` | 商品 ID |
| `order_amount` | 订单金额 |
| `commission_rate` | 佣金比例 |
| `commission_amount` | 佣金金额 |
| `status` | 状态：0 已计算，1 已结算，2 已取消 |
| `settled_time` | 结算时间 |
| `remark` | 备注 |
| `deleted` | 删除标记 |

---

## 状态流转

当前一版只支持最简单的状态流转：

- `0 已计算`
- `1 已结算`
- `2 已取消`（预留，当前没有专门取消接口）

当前只允许：
- 从 `已计算` → `已结算`

---

## 业务规则

### 1. 一个订单只允许一条分销记录
通过 `order_id` 唯一约束保证。

### 2. 佣金比例必须大于 0
如果小于等于 0，直接返回业务异常。

### 3. 只允许“已完成订单”生成分销记录
当前规则：
- 订单状态必须等于 `3`
- 即：`已完成`

### 4. 只有“已计算”状态可以结算
如果已经结算过，再次结算会报错。

---

## 接口说明

基础路径：

- `/api/distribution`

---

### 1. 计算分销记录

**POST** `/api/distribution/calculate`

#### 请求示例

```json
{
  "orderId": 1,
  "distributorUserId": 2001,
  "commissionRate": 12.5,
  "remark": "首单分销"
}
```

#### 处理逻辑

服务端会：
1. 校验佣金比例是否大于 0
2. 校验该订单是否已存在分销记录
3. 调用 `order-service` 查询订单
4. 校验订单状态是否为“已完成”
5. 计算佣金金额：

```text
commissionAmount = orderAmount * commissionRate / 100
```

6. 写入 `t_distribution_record`

#### 成功响应示例

```json
{
  "code": 200,
  "message": "分销记录创建成功",
  "data": 1
}
```

其中 `data` 为新生成的 `distributionId`。

---

### 2. 查询分销详情

**GET** `/api/distribution/{distributionId}`

#### 示例

```bash
curl -X GET http://localhost:8090/api/distribution/1
```

#### 返回内容
- 分销记录基础信息
- 订单金额、佣金比例、佣金金额
- 当前状态
- 状态文本
- 结算时间

---

### 3. 按订单查询分销记录

**GET** `/api/distribution/order/{orderId}`

#### 示例

```bash
curl -X GET http://localhost:8090/api/distribution/order/1
```

适合按订单追踪分销情况。

---

### 4. 按分销用户分页查询

**GET** `/api/distribution/user/{distributorUserId}/list`

#### 示例

```bash
curl -X GET "http://localhost:8090/api/distribution/user/2001/list?current=1&size=10"
```

可选状态过滤：

```bash
curl -X GET "http://localhost:8090/api/distribution/user/2001/list?status=0&current=1&size=10"
```

#### 用途
- 查询某个分销用户的分销订单列表
- 可按状态筛选“已计算 / 已结算”记录

---

### 5. 结算分销记录

**POST** `/api/distribution/{distributionId}/settle`

#### 正常结算示例

```bash
curl -X POST http://localhost:8090/api/distribution/1/settle
```

#### 回滚演示示例

```bash
curl -X POST "http://localhost:8090/api/distribution/1/settle?simulateRollback=true"
```

#### 当前行为
当前会：
- 先调用 `user-service` 增加分销用户余额
- 在全局事务内把状态从 `0 已计算` 改成 `1 已结算`
- 写入 `settledTime`

#### 当前事务链路
- `distribution-service` 本地更新分销状态
- `user-service` 增加余额
- 通过 `@GlobalTransactional` 包裹为一条 Seata 事务链路

#### 当前仍不做
- 不做提现
- 不做消息通知
- 不做结算任务调度

---

## 与其他服务的关系

### 依赖的服务
当前已依赖：
- `order-service`
- `user-service`

用途：
- `order-service`：查询订单是否存在、获取订单金额/商品 ID/下单用户 ID、判断订单是否已完成
- `user-service`：在分销结算时增加分销用户余额

### 当前未联动的服务
- `payment-service`
- `message-service`

这些会在后续版本继续接入。

---

## 启动

### 1. 初始化数据库
确保执行过：

```bash
mysql -h localhost -u root -proot -e "source D:/coding_file/view/scripts/mysql/init.sql"
```

### 2. 启动依赖服务
建议至少先启动：
- Nacos
- MySQL
- `order-service`

### 3. 启动 distribution-service

```bash
mvn -pl distribution-service spring-boot:run
```

---

## 编译验证

当前已验证：

```bash
mvn -pl distribution-service -am compile
mvn compile
```

都已通过。

---

## 推荐测试顺序

建议按下面顺序验证：

1. 在 `order-service` 中准备一条“已完成”订单
2. 调用 `/calculate`
3. 调用 `/api/distribution/{id}` 查详情
4. 调用 `/api/distribution/order/{orderId}` 按订单查询
5. 调用 `/user/{distributorUserId}/list` 查分页列表
6. 调用 `/settle`
7. 再次查询详情，确认状态和 `settledTime`

---

## 下一阶段：Seata 扩展

当前这个服务已经为 Seata 留好了最自然的升级点。

### 推荐下一步
把结算接口从：

- 只更新本地分销状态

扩展为：

- 更新分销状态
- 同时调用 `user-service` 增加分销用户余额
- 使用 Seata 保证这两步是一个全局事务

### 下一阶段建议目标

#### 第一阶段
- `settle` 调用 `user-service` 加余额
- 接入 `seata-spring-boot-starter`
- 为结算链路增加 `@GlobalTransactional`

#### 第二阶段
- 增加结算失败回滚演示
- 增加消息通知
- 增加分销结算统计

### 为什么当前先不做 Seata
因为当前第一版只有：
- 本地分销表写入
- 远程只读订单查询

这时引入 Seata 没有真实收益。

Seata 最适合在“跨服务写操作”出现时加入，这样学习价值最高。

---

## 关键文件

- `distribution-service/src/main/java/com/learning/distribution/DistributionServiceApplication.java`
- `distribution-service/src/main/java/com/learning/distribution/controller/DistributionController.java`
- `distribution-service/src/main/java/com/learning/distribution/service/impl/DistributionServiceImpl.java`
- `distribution-service/src/main/java/com/learning/distribution/feign/OrderFeignClient.java`
- `distribution-service/src/main/java/com/learning/distribution/entity/DistributionRecordDO.java`
- `scripts/mysql/init.sql`
