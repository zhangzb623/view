# Continue-On-Another-PC Guide

本文档用于帮助你在另一台电脑上继续当前项目，不需要重新梳理上下文。

适用场景：
- 你换了一台电脑继续开发
- 你想快速恢复当前工作进度
- 你想知道哪些已经完成，哪些仍然是下一步重点

---

## 1. 当前总体状态

当前仓库已经推进到：

- `chat-server` 一期已完成
- 全工程 `mvn compile` 已打通
- `distribution-service` 已落地第一版，并继续扩展到 Seata 结算链路与回滚演示
- `scheduler-service` 已切换到官方 `xxl-job-core` 接法
- 根 README / 中文 README 已大幅同步到当前状态
- WSL2 / Win10 本机运行文档已补充
- 数据库初始化脚本（`init.sql` / `xxl-job.sql` / `seata.sql`）已修复一轮

一句话概括当前状态：

> 当前项目已经不再是“搭骨架”阶段，而是进入了“本机运行验证、链路联调、细化文档与下一阶段增强”的阶段。

---

## 2. 你当前已经完成的关键事项

### 2.1 chat-server
已完成：
- 登录
- 心跳
- 单聊
- 群聊
- 已读回执
- 登出
- 错误回包
- 长度前缀 + JSON 协议
- 回包字段收口：`requestId` / `messageId` / `ackTimestamp` / `deliveredTo` / `deliveredCount`
- 文档补齐
- TCP Python 测试脚本落地

关键文件：
- `chat-server/README.md`
- `chat-server/PROTOCOL.md`
- `chat-server/TESTING.md`
- `chat-server/TCP-TEST-SCRIPT-GUIDE.md`
- `chat-server/scripts/chat_test_client.py`

---

### 2.2 distribution-service
当前已完成：

#### 第一阶段
- 新建 `distribution-service` 模块
- 分销记录表 `t_distribution_record`
- 分销记录计算接口
- 分销记录查询接口
- 分销记录结算接口
- 编译通过并接入根工程

#### 第二阶段
- `settle` 调用 `user-service` 增加余额
- 接入 Seata starter
- 加上 `@GlobalTransactional`
- 形成本地状态更新 + 跨服务余额变更链路

#### 第三阶段
- 增加 `simulateRollback=true` 回滚演示开关
- 用于复盘 Seata 回滚行为

关键文件：
- `distribution-service/README.md`
- `distribution-service/TESTING.md`
- `distribution-service/src/main/java/com/learning/distribution/controller/DistributionController.java`
- `distribution-service/src/main/java/com/learning/distribution/service/impl/DistributionServiceImpl.java`
- `distribution-service/src/main/java/com/learning/distribution/feign/OrderFeignClient.java`
- `distribution-service/src/main/java/com/learning/distribution/feign/UserFeignClient.java`
- `distribution-service/src/main/resources/application.yml`

---

### 2.3 scheduler-service
当前已完成：
- 从第三方 starter 切换到官方 `com.xuxueli:xxl-job-core`
- 新增 `XxlJobConfig`
- handler 改回官方 `@XxlJob` + `XxlJobHelper`
- 编译通过
- README 已按当前接法补齐

关键文件：
- `scheduler-service/README.md`
- `scheduler-service/src/main/java/com/learning/scheduler/config/XxlJobConfig.java`
- `scheduler-service/src/main/java/com/learning/scheduler/handler/TestTaskHandler.java`
- `scheduler-service/src/main/java/com/learning/scheduler/handler/MailNotificationTaskHandler.java`
- `scheduler-service/src/main/resources/application.yml`

---

### 2.4 数据库脚本
当前已修复：

#### `scripts/mysql/init.sql`
- 修复新增 `distribution_db` 后，测试数据插入前没有切回正确数据库的问题
- 现在已补回：
  - `USE user_db;`
  - `USE product_db;`
  - `USE payment_db;`

#### `scripts/mysql/seata.sql`
- 去掉不合理字段与错误索引
- 去掉伪造初始化事务数据
- 保留更干净的 Seata 三张核心表初始化

#### `scripts/mysql/xxl-job.sql`
- 补齐：
  - `xxl_job_log_report`
  - `xxl_job_lock`
- 改成可重复执行：
  - `CREATE TABLE IF NOT EXISTS`
  - `INSERT IGNORE`
- 并进一步对齐到更接近当前 `xxl-job-admin` 结构的 `xxl_job_group`

---

### 2.5 全工程编译
当前已经验证：

```bash
mvn compile
```

可以通过。

这意味着：
- 当前所有模块至少在编译层面已经打通
- 后续重点不再是“修 compile”，而是“本机运行、联调、验证和继续增强”

---

## 3. 关键文档索引

### 项目总览 / 当前状态
- `README.md`
- `README_CN.md`
- `docs/Progress-Summary.md`
- `docs/Next-Steps-After-Compile-Success.md`

### WSL2 / 本机运行
- `docs/WSL2-Ubuntu24-Deployment-CN.md`
- `docs/Win10-WSL2-Distribution-Seata-Runbook-CN.md`

### chat-server
- `chat-server/README.md`
- `chat-server/PROTOCOL.md`
- `chat-server/TESTING.md`
- `chat-server/TCP-TEST-SCRIPT-GUIDE.md`

### distribution-service
- `distribution-service/README.md`
- `distribution-service/TESTING.md`

### scheduler-service
- `scheduler-service/README.md`

### 二期规划
- `docs/future-features/README.md`
- `docs/future-features/chat-server-phase-2.md`

---

## 4. 换电脑后第一步怎么做

### 4.1 拉取代码
在新电脑上先同步到最新仓库代码。

### 4.2 准备基础环境
建议环境：
- Windows 10 / 11
- WSL2 Ubuntu 24.04
- JDK 17
- Maven 3.8+
- Docker Desktop

### 4.3 确认 Java / Maven

```bash
java -version
mvn -version
```

重点确认：
- `java` 是 17
- `mvn` 用的也是 JDK 17

### 4.4 起基础中间件
当前最推荐先起：
- MySQL
- Redis
- MongoDB
- Nacos
- Seata Server
- xxl-job-admin（如果你要继续联调 scheduler-service）

---

## 5. 数据库脚本重跑顺序

如果是新电脑，建议按这个顺序跑：

```bash
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/init.sql
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/xxl-job.sql
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/seata.sql
```

### 如果之前跑残了，建议重建

#### 重建 `xxl_job`
```bash
docker exec -it learning-mysql mysql -uroot -proot -e "DROP DATABASE IF EXISTS xxl_job;"
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/xxl-job.sql
```

#### 重建 `seata`
```bash
docker exec -it learning-mysql mysql -uroot -proot -e "DROP DATABASE IF EXISTS seata;"
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/seata.sql
```

---

## 6. 当前最值得先跑通的本机链路

如果你换电脑后只想先恢复最有价值的一条链路，最推荐：

### distribution / Seata 这一条
启动：
- `user-service`
- `order-service`
- `distribution-service`

然后按文档走：
- `distribution-service/TESTING.md`
- `docs/Win10-WSL2-Distribution-Seata-Runbook-CN.md`

### 推荐动作顺序
1. 准备一条 `status = 3` 的订单
2. 调 `/calculate`
3. 调 `/settle`
4. 查 `distribution_db.t_distribution_record`
5. 查 `user_db.t_user_info.balance`
6. 调 `/settle?simulateRollback=true`
7. 观察是否回滚

---

## 7. 当前最自然的下一步开发方向

如果你换电脑后继续开发，我建议优先顺序如下：

### 第一优先级：本机联调验证
先把以下真正跑起来：
- `distribution-service` + `user-service` + `order-service` + Seata
- `scheduler-service` + `xxl-job-admin`

### 第二优先级：补测试 / 运行文档
当前文档已经很多，但还可以继续收口：
- 根 README 的 Quick Start
- `scheduler-service/TESTING.md`
- 数据库脚本执行后验证清单

### 第三优先级：继续扩展功能
当前最适合继续增强的是：

#### A. distribution-service
- 做更完整的 Seata 回滚联调验证
- 增加分销消息通知（Kafka / RocketMQ）
- 增加分销统计接口

#### B. scheduler-service
- 把 handler 真正连到数据库/业务逻辑
- 联调 admin 创建执行器和任务
- 验证 `@XxlJob` 任务触发

#### C. chat-server 二期
- Redis 会话共享
- 离线消息
- 历史消息查询

---

## 8. 当前已知的注意事项

### 8.1 user-service
之前有过配置结构问题：
- 重复顶层 `spring:`
- `mybatis-plus` 缩进错误

现在已修复。
关键文件：
- `user-service/src/main/resources/application.yml`

### 8.2 xxl-job
当前已经从第三方 starter 改成官方 core 接法。
但你本机是否能真正顺滑联调，还取决于：
- `xxl-job-admin` 镜像版本
- `scripts/mysql/xxl-job.sql`
- 本机容器实际运行状态

### 8.3 Seata
当前代码已经有 Seata 链路和回滚演示开关，但真实回滚效果仍取决于：
- Seata Server 是否启动
- 数据源 / 注册配置是否完整
- 运行时配置是否一致

---

## 9. 当前已经完成的任务结论

可以把现在的项目阶段理解为：

- ✅ chat-server 一期完成
- ✅ 全工程编译通过
- ✅ distribution-service 第一版 + Seata 第二阶段 + 回滚演示已完成
- ✅ scheduler-service 官方 xxl-job-core 接法已完成
- ✅ 数据库初始化脚本已修整一轮
- 🔄 当前进入本机联调 / 运行验证 / 继续增强阶段

---

## 10. 如果你下一次回来继续，最建议先看什么

优先按这个顺序看：

1. `docs/Continue-On-Another-PC-Guide.md`
2. `docs/Progress-Summary.md`
3. `docs/Win10-WSL2-Distribution-Seata-Runbook-CN.md`
4. `distribution-service/TESTING.md`
5. `scheduler-service/README.md`
6. `docs/WSL2-Ubuntu24-Deployment-CN.md`

这样基本就能在另一台电脑上快速接上当前节奏。
