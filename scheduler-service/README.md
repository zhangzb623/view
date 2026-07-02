# Scheduler Service

调度服务用于演示：
- `xxl-job` 分布式任务调度
- MongoDB 任务日志存储
- 官方 `xxl-job-core` 接法

当前这版已经不再依赖第三方 starter，而是改成：
- `com.xuxueli:xxl-job-core`
- 自定义 `XxlJobConfig`
- 官方 `@XxlJob` + `XxlJobHelper`

这更适合学习和复盘，因为能直接看到 executor 配置和 handler 注册方式。

---

## 当前能力

### 已完成
- Spring Boot 服务骨架
- MongoDB 任务日志服务
- 任务日志查询与统计接口
- 官方 `xxl-job-core` 接入
- 自定义 `XxlJobSpringExecutor` 配置
- 两类任务 handler：
  - `TestTaskHandler`
  - `MailNotificationTaskHandler`

### 当前范围内不做
- 真正的邮件发送
- 真正的订单超时清理数据库逻辑
- 真正的报表文件生成
- 多节点执行器集群治理
- 复杂失败重试策略

---

## 服务端口

- `8087`

---

## 核心接入方式

当前项目已经切换为官方接法：

### Maven 依赖

`scheduler-service/pom.xml` 使用：

```xml
<dependency>
    <groupId>com.xuxueli</groupId>
    <artifactId>xxl-job-core</artifactId>
</dependency>
```

### 配置类

执行器配置类：

- `scheduler-service/src/main/java/com/learning/scheduler/config/XxlJobConfig.java`

当前会显式创建：
- `XxlJobSpringExecutor`

并绑定这些配置：
- `xxl.job.admin.addresses`
- `xxl.job.executor.appname`
- `xxl.job.executor.ip`
- `xxl.job.executor.port`
- `xxl.job.executor.logpath`
- `xxl.job.executor.logretentiondays`
- `xxl.job.accessToken`

### Handler 注册方式

当前 handler 使用官方注解：

- `@XxlJob("testJob")`
- `@XxlJob("statisticsJob")`
- `@XxlJob("orderTimeoutJob")`
- `@XxlJob("cacheCleanupJob")`
- `@XxlJob("reportJob")`
- `@XxlJob("sendDailyReportJob")`

并使用：
- `XxlJobHelper.getJobParam()`
- `XxlJobHelper.getJobId()`
- `XxlJobHelper.handleSuccess(...)`
- `XxlJobHelper.handleFail(...)`

---

## 配置说明

配置文件：
- `scheduler-service/src/main/resources/application.yml`

当前关键配置：

```yaml
xxl:
  job:
    admin:
      addresses: http://localhost:8080/xxl-job-admin
    executor:
      appname: scheduler-service
      ip:
      port: 9999
      logpath: /data/applogs/xxl-job/jobhandler
      logretentiondays: 30
    accessToken: ''
```

### 字段含义

| 配置项 | 说明 |
| --- | --- |
| `admin.addresses` | xxl-job-admin 地址 |
| `executor.appname` | 执行器 appname，必须和管理台配置一致 |
| `executor.ip` | 执行器 IP，留空时由框架自行处理 |
| `executor.port` | 执行器端口 |
| `executor.logpath` | 任务日志路径 |
| `executor.logretentiondays` | 日志保留天数 |
| `accessToken` | admin / executor 通信令牌 |

---

## 当前任务处理器

### 1. TestTaskHandler

文件：
- `scheduler-service/src/main/java/com/learning/scheduler/handler/TestTaskHandler.java`

当前提供：
- `testJob`
- `statisticsJob`
- `orderTimeoutJob`
- `cacheCleanupJob`
- `reportJob`

这些任务当前主要用于：
- 演示任务执行流程
- 演示 handler 注册方式
- 演示成功/失败回调

### 2. MailNotificationTaskHandler

文件：
- `scheduler-service/src/main/java/com/learning/scheduler/handler/MailNotificationTaskHandler.java`

当前提供：
- `sendDailyReportJob`

当前行为：
- 模拟获取待发邮件用户
- 模拟发送邮件
- 通过 `XxlJobHelper` 返回执行结果

---

## MongoDB 日志相关

调度服务除了 xxl-job 执行器，还带有一套 MongoDB 任务日志演示。

### 关键目录
- `scheduler-service/src/main/java/com/learning/scheduler/entity/`
- `scheduler-service/src/main/java/com/learning/scheduler/repository/`
- `scheduler-service/src/main/java/com/learning/scheduler/service/`
- `scheduler-service/src/main/java/com/learning/scheduler/controller/`

### 用途
- 记录任务执行日志
- 按任务名 / 状态查询日志
- 做成功 / 失败统计

这部分更偏“管理 / 查询 / 统计演示”，不是 xxl-job-admin 内置表的替代品。

---

## 数据库与中间件依赖

### 必要依赖
- MySQL（供 xxl-job-admin 使用 `xxl_job` 库）
- MongoDB（供 scheduler-service 自己的任务日志使用）
- xxl-job-admin

### 脚本
- `scripts/mysql/xxl-job.sql`

当前脚本已经整理为：
- 可重复执行
- 补齐 `xxl_job_log_report`
- 补齐 `xxl_job_lock`
- 对齐当前 admin 常见字段结构

---

## 启动前准备

### 1. 初始化 xxl-job 数据库

```bash
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/xxl-job.sql
```

### 2. 启动 xxl-job-admin

如果你是按 WSL2 文档走本机容器方式，确认 `xxl-job-admin` 已启动。

默认访问地址通常是：

```text
http://localhost:8080/xxl-job-admin
```

默认管理员账号：
- 用户名：`admin`
- 密码：`123456`

### 3. 启动 MongoDB

因为 scheduler-service 里自带 MongoDB 日志功能。

---

## 启动 Scheduler Service

```bash
mvn -pl scheduler-service spring-boot:run
```

---

## 如何在 xxl-job-admin 里配置执行器

登录 admin 后：

### 1）新增执行器
建议配置：
- AppName：`scheduler-service`
- 名称：`Scheduler Service Executor`
- 注册方式：自动注册

这里最关键的是：
- **AppName 必须和 `application.yml` 里的 `xxl.job.executor.appname` 一致**

也就是：

```yaml
appname: scheduler-service
```

### 2）新增任务
你可以新增任务时把 `JobHandler` 填成下面这些值之一：

- `testJob`
- `statisticsJob`
- `orderTimeoutJob`
- `cacheCleanupJob`
- `reportJob`
- `sendDailyReportJob`

---

## 推荐你先验证哪个任务

最推荐先验证：

### 1. `testJob`
因为它最简单，最适合先确认：
- admin 和 executor 是否联通
- handler 名称是否正确
- 执行日志是否能正常返回

### 2. `statisticsJob`
第二个可以测这个，用于确认：
- 复杂返回内容是否正常
- 执行成功日志是否正常

---

## 最小联调步骤

建议按这个顺序：

1. 起 MySQL
2. 执行 `scripts/mysql/xxl-job.sql`
3. 起 xxl-job-admin
4. 起 MongoDB
5. 起 `scheduler-service`
6. 登录 admin
7. 新增执行器：`scheduler-service`
8. 新建任务，Handler 填 `testJob`
9. 手动触发一次

### 预期
- 页面上任务触发成功
- scheduler-service 日志里能看到执行日志
- admin 里能看到执行结果

---

## 常见问题

### 1. `Table 'xxl_job.xxl_job_log_report' doesn't exist`
说明旧版 `scripts/mysql/xxl-job.sql` 缺失表。

当前仓库已修复，直接：
- 删除旧 `xxl_job` 库
- 重跑最新版脚本

### 2. `Unknown column 't.address_list' in 'field list'`
说明 admin 镜像版本和表结构版本不一致。

当前仓库已经把 `xxl_job_group` 调整到更接近当前 admin 结构。
如果你之前初始化过旧表结构，建议：
- 删除旧 `xxl_job` 库
- 重新执行 `scripts/mysql/xxl-job.sql`

### 3. admin 看不到执行器
重点检查：
- admin 是否启动
- `appname` 是否一致
- `executor.port` 是否被占用
- WSL2 / Docker 端口是否可达

### 4. 任务触发了但无日志
重点检查：
- `JobHandler` 是否和 `@XxlJob("...")` 名称一致
- `scheduler-service` 是否真正启动成功
- `logpath` 是否可写

---

## 关键文件

- `scheduler-service/pom.xml`
- `scheduler-service/src/main/java/com/learning/scheduler/config/XxlJobConfig.java`
- `scheduler-service/src/main/java/com/learning/scheduler/handler/TestTaskHandler.java`
- `scheduler-service/src/main/java/com/learning/scheduler/handler/MailNotificationTaskHandler.java`
- `scheduler-service/src/main/resources/application.yml`
- `scripts/mysql/xxl-job.sql`
- `docs/WSL2-Ubuntu24-Deployment-CN.md`
