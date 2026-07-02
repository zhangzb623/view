# Win10 + WSL2 Distribution / Seata 最短执行清单

本文档专门面向你当前这种环境：

- Windows 10
- WSL2 Ubuntu
- 本机本地学习 / 调试
- 优先验证：
  - `user-service`
  - `order-service`
  - `distribution-service`
  - Seata 分销结算与回滚演示

目标不是一次把全项目都跑起来，而是先把**最有复盘价值的一条链路**跑通。

---

## 1. 本次最小目标

这次最小闭环只做下面这条链路：

1. 启动 MySQL / Redis / Nacos / Seata
2. 初始化数据库
3. 编译项目
4. 启动：
   - `user-service`
   - `order-service`
   - `distribution-service`
5. 调用：
   - `/calculate`
   - `/settle`
   - `/settle?simulateRollback=true`
6. 查数据库验证：
   - 分销状态
   - 用户余额
   - Seata 回滚效果

---

## 2. 先确认环境

### 在 WSL2 里确认 Java / Maven

```bash
java -version
mvn -version
```

你要重点确认：
- `java` 是 17
- `mvn` 用的也是 JDK 17

如果 `mvn -version` 看到的还是 JDK 8，需要先改 `JAVA_HOME`。

---

## 3. 启动基础依赖

你这条链路最少需要：
- MySQL
- Redis
- Nacos
- Seata Server

### 3.1 启动 MySQL / Redis / Nacos

如果你已经有自己的容器运行方式，就用你现有的。

如果你是按仓库里的 WSL2 文档自己建的 compose，中间件先起来。

先确认这些端口可用：
- MySQL: `3306`
- Redis: `6379`
- Nacos: `8848`
- Seata: 默认 `8091`

### 3.2 启动 Seata Server

如果你现在还没起 Seata Server，这条链路里的回滚演示就看不到真正效果。

建议你先确保本机上已经有 Seata Server 容器或进程。

如果后面你要，我可以再帮你单独补一份 Seata Server 在 Win10 / WSL2 里的启动说明。

---

## 4. 初始化数据库

你已经修复了 `init.sql`，现在直接执行最新版：

```bash
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/init.sql
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/xxl-job.sql
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/seata.sql
```

### 验证数据库

```bash
docker exec -it learning-mysql mysql -uroot -proot -e "SHOW DATABASES;"
```

你至少应该看到：
- `user_db`
- `product_db`
- `order_db`
- `payment_db`
- `distribution_db`
- `xxl_job`
- `seata`

再确认分销表：

```bash
docker exec -it learning-mysql mysql -uroot -proot -e "SHOW TABLES FROM distribution_db;"
```

应该能看到：
- `t_distribution_record`

---

## 5. 先编译一次

在项目根目录执行：

```bash
mvn compile
```

当前这一步应该已经能通过，因为仓库 compile 已打通。

如果你本机上 `spring-boot:run` 遇到本地模块解析问题，可以先执行：

```bash
mvn install -DskipTests
```

再启动服务。

---

## 6. 启动最小链路服务

推荐顺序：

### 1）user-service

```bash
mvn -pl user-service spring-boot:run
```

### 2）order-service

```bash
mvn -pl order-service spring-boot:run
```

### 3）distribution-service

```bash
mvn -pl distribution-service spring-boot:run
```

---

## 7. 准备一条已完成订单

`distribution-service` 当前规则要求：
- 只有订单状态 = `3`
- 即“已完成”

所以你必须先准备一条已完成订单。

### 方法 1：如果你本机已有订单测试数据
直接查：

```bash
docker exec -it learning-mysql mysql -uroot -proot -e "SELECT order_id, user_id, product_id, total_price, status FROM order_db.t_order LIMIT 10;"
```

看是否有 `status = 3` 的订单。

### 方法 2：手动改一条订单状态
如果没有已完成订单，你可以临时改一条：

```bash
docker exec -it learning-mysql mysql -uroot -proot -e "UPDATE order_db.t_order SET status = 3 WHERE order_id = 1;"
```

然后再查确认：

```bash
docker exec -it learning-mysql mysql -uroot -proot -e "SELECT order_id, status FROM order_db.t_order WHERE order_id = 1;"
```

---

## 8. 调用分销计算接口

### 创建分销记录

```bash
curl -X POST http://localhost:8090/api/distribution/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "distributorUserId": 2,
    "commissionRate": 12.5,
    "remark": "WSL2 本机演示"
  }'
```

### 预期
- 返回成功
- `data` 是新的 `distributionId`

### 查数据库确认

```bash
docker exec -it learning-mysql mysql -uroot -proot -e "SELECT distribution_id, order_id, distributor_user_id, commission_rate, commission_amount, status FROM distribution_db.t_distribution_record;"
```

你应看到：
- 新增一条记录
- `status = 0`
- `commission_amount` 已计算出来

---

## 9. 先做正常结算演示

### 调用结算接口

```bash
curl -X POST http://localhost:8090/api/distribution/1/settle
```

这里把 `1` 换成你刚生成的真实 `distributionId`。

### 结算后查分销记录

```bash
docker exec -it learning-mysql mysql -uroot -proot -e "SELECT distribution_id, status, settled_time FROM distribution_db.t_distribution_record WHERE distribution_id = 1;"
```

### 查用户余额

```bash
docker exec -it learning-mysql mysql -uroot -proot -e "SELECT user_id, username, balance FROM user_db.t_user_info WHERE user_id = 2;"
```

### 预期
- 分销记录状态变成 `1`
- `settled_time` 有值
- `user_id = 2` 的余额增加

---

## 10. 再做 Seata 回滚演示

### 调用回滚演示接口

```bash
curl -X POST "http://localhost:8090/api/distribution/1/settle?simulateRollback=true"
```

注意：
- 要换成一条仍然处于“待结算”的分销记录
- 已经结算过的记录不能直接拿来演示回滚

所以更建议你：
1. 先再创建一条新的分销记录
2. 再对那条记录执行 `simulateRollback=true`

### 调用后检查分销记录

```bash
docker exec -it learning-mysql mysql -uroot -proot -e "SELECT distribution_id, status, settled_time FROM distribution_db.t_distribution_record WHERE distribution_id = 2;"
```

### 再检查用户余额

```bash
docker exec -it learning-mysql mysql -uroot -proot -e "SELECT user_id, username, balance FROM user_db.t_user_info WHERE user_id = 2;"
```

### 预期
如果 Seata 运行配置完整：
- 分销记录不应变成已结算
- 用户余额不应真的增加

这就是你当前最有价值的 Seata 回滚复盘点。

---

## 11. 如果回滚演示没生效，优先检查什么

如果你发现：
- 分销状态回滚了，但余额没回滚
- 或者余额增加了但状态没回滚
- 或者两边都没受控

优先检查：

### 1）Seata Server 是否真的启动了

### 2）`user-service` 和 `distribution-service` 是否都加载了 Seata 配置

### 3）数据库是否使用了支持 Seata 的正确数据源接线

### 4）Nacos / Seata 的注册配置是否一致

也就是说：
- 当前代码链路已经准备好了
- 但真实回滚效果还取决于你本机运行环境里的 Seata Server 和配置是否完整

---

## 12. 最短复盘顺序

如果你只想最快复盘一次，按这个顺序：

1. `mvn compile`
2. 起 MySQL / Redis / Nacos / Seata
3. 起：
   - `user-service`
   - `order-service`
   - `distribution-service`
4. 准备一条 `status = 3` 的订单
5. 调 `/calculate`
6. 查 `distribution_db`
7. 调 `/settle`
8. 查 `distribution_db + user_db`
9. 再做一次 `simulateRollback=true`
10. 查两边是否都回滚

---

## 13. 你现在最该先看哪几个文件

- `distribution-service/README.md`
- `distribution-service/TESTING.md`
- `docs/WSL2-Ubuntu24-Deployment-CN.md`
- `docs/Progress-Summary.md`
