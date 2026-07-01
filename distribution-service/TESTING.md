# Distribution Service Testing Guide

本文档用于帮助你验证 `distribution-service` 当前已经完成的两阶段能力：

- 第一阶段：分销记录计算、落库、查询、结算
- 第二阶段：结算时联动 `user-service` 增加余额，并通过 Seata 包裹事务链路

---

## 1. 当前验证目标

建议重点验证：
- 分销记录能否正确创建
- 同一订单是否禁止重复创建分销记录
- 只有已完成订单能否生成分销记录
- 分销记录能否按 ID / 订单 / 分销用户查询
- 结算时是否会调用 `user-service` 增加余额
- 结算后状态是否改成已结算
- 事务异常时是否能正确失败

---

## 2. 当前接口概览

基础路径：

- `/api/distribution`

当前接口：
- `POST /calculate`
- `GET /{distributionId}`
- `GET /order/{orderId}`
- `GET /user/{distributorUserId}/list`
- `POST /{distributionId}/settle`

---

## 3. 依赖服务

当前需要至少启动：
- MySQL
- Nacos
- `user-service`
- `order-service`
- `distribution-service`

---

## 4. 启动顺序建议

### 1）启动基础依赖
- MySQL
- Nacos

### 2）启动用户服务

```bash
mvn -pl user-service spring-boot:run
```

### 3）启动订单服务

```bash
mvn -pl order-service spring-boot:run
```

### 4）启动分发服务

```bash
mvn -pl distribution-service spring-boot:run
```

---

## 5. 测试前准备

### 1）初始化数据库
确保已经执行：

```bash
mysql -h localhost -u root -proot -e "source D:/coding_file/view/scripts/mysql/init.sql"
```

### 2）准备一条“已完成订单”
当前分销规则要求：
- 订单状态必须为 `3`
- 即“已完成”

如果没有这类订单，你需要先在 `order-service` 里准备一条已完成订单数据，或者直接往测试库里插一条符合规则的记录。

---

## 6. 计算分销记录

### 接口

**POST** `http://localhost:8090/api/distribution/calculate`

### 请求示例

```bash
curl -X POST http://localhost:8090/api/distribution/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "distributorUserId": 2001,
    "commissionRate": 12.5,
    "remark": "首单分销"
  }'
```

### 预期行为

服务端会：
1. 校验佣金比例是否大于 0
2. 校验该订单是否已存在分销记录
3. 调用 `order-service` 查询订单
4. 校验订单状态是否为 `3`
5. 计算佣金金额
6. 写入 `t_distribution_record`

### 成功响应示例

```json
{
  "code": 200,
  "message": "分销记录创建成功",
  "data": 1
}
```

其中 `data` 为 `distributionId`。

### 你应该重点观察什么

- 是否返回了新的 `distributionId`
- 数据库里是否新增了一条记录
- `commissionAmount` 是否等于：

```text
orderAmount * commissionRate / 100
```

---

## 7. 查询分销详情

### 按分销 ID 查询

```bash
curl -X GET http://localhost:8090/api/distribution/1
```

### 预期返回

应包含：
- `distributionId`
- `orderId`
- `orderUserId`
- `distributorUserId`
- `productId`
- `orderAmount`
- `commissionRate`
- `commissionAmount`
- `status`
- `statusText`
- `settledTime`
- `remark`

### 重点校验

- `status = 0`
- `statusText = 已计算`
- `settledTime = null`

---

## 8. 按订单查询分销记录

### 接口

```bash
curl -X GET http://localhost:8090/api/distribution/order/1
```

### 适合验证的点

- 某一订单能否追踪到对应分销记录
- 唯一订单约束是否已生效

---

## 9. 按分销用户分页查询

### 接口

```bash
curl -X GET "http://localhost:8090/api/distribution/user/2001/list?current=1&size=10"
```

### 按状态筛选

```bash
curl -X GET "http://localhost:8090/api/distribution/user/2001/list?status=0&current=1&size=10"
```

### 重点校验

- 分页参数是否生效
- 同一分销用户的记录是否能正确返回
- 状态筛选是否生效

---

## 10. 结算分销记录（Seata 第二阶段）

### 接口

**POST** `http://localhost:8090/api/distribution/{distributionId}/settle`

### 正常结算示例

```bash
curl -X POST http://localhost:8090/api/distribution/1/settle
```

### 回滚演示示例

```bash
curl -X POST "http://localhost:8090/api/distribution/1/settle?simulateRollback=true"
```

### 当前实际行为

现在这一步已经不是“只改本地状态”了，而是：

1. 先调用 `user-service` 的 `/api/user/balance/add`
2. 给分销用户增加余额
3. 在全局事务中把分销记录状态改成 `1 已结算`
4. 写入 `settledTime`

### 预期结果

#### 分销服务侧
- `status = 1`
- `statusText = 已结算`
- `settledTime` 已有值

#### user-service 侧
- `distributorUserId` 对应用户余额增加
- 增加金额 = `commissionAmount`

---

## 11. 如何验证 Seata 链路是否生效

当前代码里已经接入：
- `distribution-service` 的 `@GlobalTransactional`
- `user-service` 的 Seata starter

### 你至少要验证这条业务链是否都走到了

#### 方法 1：观察业务结果
执行 `/settle` 后，检查两边：

1. `distribution_db.t_distribution_record`
   - 状态是否变成已结算
2. `user_db.t_user_info`
   - 对应分销用户余额是否增加

如果这两步都发生，说明事务链路至少业务上已经串通。

### 建议 SQL

#### 查分销记录

```sql
SELECT distribution_id, order_id, distributor_user_id, commission_amount, status, settled_time
FROM distribution_db.t_distribution_record
WHERE distribution_id = 1;
```

#### 查用户余额

```sql
SELECT user_id, username, balance
FROM user_db.t_user_info
WHERE user_id = 2001;
```

---

## 12. 如何做失败场景验证

Seata 的真正价值是“跨服务写操作失败时不一致如何处理”。

当前你可以先做“业务失败验证”，再做“事务失败演练”。

### 场景 1：重复结算

对同一条已结算记录再次调用：

```bash
curl -X POST http://localhost:8090/api/distribution/1/settle
```

### 预期
- 返回业务异常
- 提示：只有待结算记录可结算

---

### 场景 2：无效佣金比例

```bash
curl -X POST http://localhost:8090/api/distribution/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 2,
    "distributorUserId": 2001,
    "commissionRate": 0,
    "remark": "非法比例"
  }'
```

### 预期
- 返回业务异常
- 提示佣金比例必须大于 0

---

### 场景 3：重复订单创建分销记录

对同一订单重复调用 `/calculate`

### 预期
- 返回业务异常
- 提示该订单已存在分销记录

---

### 场景 4：订单未完成

如果订单状态不是 `3`，调用 `/calculate`

### 预期
- 返回业务异常
- 提示仅已完成订单可生成分销记录

---

## 13. 内置 Seata 回滚演示

当前代码已经内置了一个可控参数：

- `simulateRollback=true`

### 调用方式

```bash
curl -X POST "http://localhost:8090/api/distribution/1/settle?simulateRollback=true"
```

### 行为说明

服务端会：
1. 先调用 `user-service.addBalance(...)`
2. 然后故意抛出：

```java
throw new RuntimeException("mock seata rollback")
```

### 预期现象

如果 Seata 运行配置完整：
- `distribution_db.t_distribution_record` 不应变成已结算
- `user_db.t_user_info.balance` 不应真正增加

这就是当前仓库里已经具备的“分布式事务回滚演示开关”。

---

## 14. 当前限制说明

虽然代码已经接入了 Seata 链路，但当前仍有这些限制：

- 还没做运行期完整联调验证
- 还没验证 Seata Server 的实际注册与协调配置
- 还没验证回滚演示在真实运行环境中的效果
- 还没做分销提现、分销消息通知、自动结算任务

所以当前更准确的定位是：
- **代码链路已接通**
- **编译已通过**
- **具备进一步联调 Seata 的基础**

---

## 15. 推荐你下一步怎么复盘

建议按这个顺序：

1. 先看 `distribution-service/README.md`
2. 再按本文档调用 `/calculate`
3. 查数据库确认记录写入
4. 再调用 `/settle`
5. 查 `distribution_db` 和 `user_db` 两边是否一起变化
6. 最后调用 `?simulateRollback=true` 做 Seata 回滚演示

---

## 16. 关键文件

- `distribution-service/src/main/java/com/learning/distribution/controller/DistributionController.java`
- `distribution-service/src/main/java/com/learning/distribution/service/impl/DistributionServiceImpl.java`
- `distribution-service/src/main/java/com/learning/distribution/feign/OrderFeignClient.java`
- `distribution-service/src/main/java/com/learning/distribution/feign/UserFeignClient.java`
- `distribution-service/src/main/resources/application.yml`
- `user-service/src/main/resources/application.yml`
- `scripts/mysql/init.sql`
