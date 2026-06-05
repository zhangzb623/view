# Scheduler Service

调度服务，演示xxl-job分布式任务调度和MongoDB存储。

## 功能列表

### 1. 任务管理
- ✅ xxl-job任务注册
- ✅ 测试任务
- ✅ 数据统计任务
- ✅ 订单超时检查任务
- ✅ 缓存清理任务
- ✅ 报表生成任务
- ✅ 邮件通知任务

### 2. 任务日志（MongoDB）
- ✅ 任务执行日志记录
- ✅ 任务执行结果保存
- ✅ 任务执行时间统计
- ✅ 任务执行次数统计
- ✅ 任务成功率统计

### 3. 任务查询
- ✅ 根据任务名称查询日志
- ✅ 根据状态查询日志
- ✅ 分页查询任务日志
- ✅ 任务执行统计

### 4. 数据统计
- ✅ 任务执行次数统计
- ✅ 任务成功/失败统计
- ✅ 任务成功率计算

## 技术实现

### 依赖技术
- **Spring Boot 3.2.0**: 基础框架
- **Spring Cloud**: 微服务架构
- **MyBatis Plus 3.5.5**: ORM框架（辅助）
- **MongoDB 6.0**: NoSQL数据库
- **xxl-job 2.4.0**: 分布式任务调度
- **Common Domain**: 基础实体类
- **Common Starter**: 通用组件

### 核心功能实现

#### 1. xxl-job任务注册
```java
@Component
public class TestTaskHandler {
    public void testJob() {
        String jobParam = XxlJobHelper.getJobParam();
        log.info("任务执行: jobId={}, jobParam={}", XxlJobHelper.getJobId(), jobParam);

        XxlJobHelper.handleSuccess(result);
    }
}
```

#### 2. xxl-job配置
```yaml
spring:
  xxl:
    job:
      admin:
        addresses: http://localhost:8080/xxl-job-admin
      executor:
        appname: scheduler-service
        port: 9999
        logpath: /data/applogs/xxl-job/jobhandler
        logretentiondays: 30
      accessToken: ''
```

#### 3. 任务执行流程
```
xxl-job admin
  → 调度任务（触发时间到达）
  → 发送任务到执行器
    → TestTaskHandler.testJob()
      → 获取任务参数
        → 执行业务逻辑
          → 返回执行结果
            → 保存到MongoDB
              → 更新执行时间
```

#### 4. MongoDB存储
```java
@Document(collection = "task_logs")
public class TaskLogDO {
    @Id
    private String jobId;
    private String jobName;
    private Integer status;
    private String handleResult;
    private Long executeTime;
    private LocalDateTime createTime;
}
```

## API接口

### 基础路径
`http://localhost:8087/api/task-log`

### 任务日志接口

#### 查询任务日志
```
GET /list?jobName=xxx&status=0&page=1&size=10
```

#### 根据任务名称查询日志
```
GET /job/{jobName}
```

#### 查询成功日志
```
GET /success
```

#### 查询失败日志
```
GET /fail
```

#### 统计任务执行次数
```
GET /count/{jobName}
```

#### 统计任务成功次数
```
GET /count/success/{jobName}
```

#### 统计任务失败次数
```
GET /count/fail/{jobName}
```

#### 获取任务统计信息
```
GET /statistics/{jobName}
```

## xxl-job配置

### 执行器配置
```yaml
spring:
  xxl:
    job:
      executor:
        appname: scheduler-service
        port: 9999
        logpath: /data/applogs/xxl-job/jobhandler
        logretentiondays: 30
```

### 任务管理
在xxl-job管理控制台创建任务：
- **JobHandler**: testJob
- **Cron**: 0 0 2 * * ? (每天凌晨2点执行)
- **运行模式**: BEAN
- **JobHandler**: testJob
- **调度机器**: 所有机器
- **阻塞处理策略**: 单机串行
- **路由策略**: FIRST

## 任务处理器

### TestTaskHandler
```java
@Component
public class TestTaskHandler {
    public void testJob() {
        // 测试任务
    }
}
```

### MailNotificationTaskHandler
```java
@Component
public class MailNotificationTaskHandler {
    public void sendDailyReportJob() {
        // 邮件通知任务
    }
}
```

### 其他任务
- **statisticsJob**: 数据统计任务
- **orderTimeoutJob**: 订单超时检查任务
- **cacheCleanupJob**: 缓存清理任务
- **reportJob**: 报表生成任务

## MongoDB Schema

### task_logs
```json
{
  "_id": "xxx",
  "jobId": "20240601123456789",
  "executorId": "scheduler-service",
  "jobName": "testJob",
  "jobParam": "{}",
  "status": 0,
  "handleResult": "{\"currentTime\":\"2024-06-01 14:30:00\"}",
  "executeTime": 1234,
  "handleMsg": null,
  "createTime": "2024-06-01T14:30:00",
  "updateTime": "2024-06-01T14:30:00",
  "deleted": 0
}
```

## 使用示例

### 查询任务日志
```bash
curl -X GET "http://localhost:8087/api/task-log/list?jobName=testJob&page=1&size=10"
```

### 查询成功日志
```bash
curl -X GET http://localhost:8087/api/task-log/success
```

### 查询失败日志
```bash
curl -X GET http://localhost:8087/api/task-log/fail
```

### 统计任务执行次数
```bash
curl -X GET http://localhost:8087/api/task-log/count/testJob
```

### 统计任务成功次数
```bash
curl -X GET http://localhost:8087/api/task-log/count/success/testJob
```

### 统计任务失败次数
```bash
curl -X GET http://localhost:8087/api/task-log/count/fail/testJob
```

### 获取任务统计信息
```bash
curl -X GET http://localhost:8087/api/task-log/statistics/testJob

# 返回: {"totalCount": 100, "successCount": 98, "failCount": 2, "successRate": "98.00%"}
```

## 注意事项

1. **xxl-job Server部署**: 需要单独部署xxl-job-admin和xxl-job-executor
2. **任务幂等性**: 任务需要保证幂等性，避免重复执行
3. **任务超时**: xxl-job支持任务超时设置，超过超时时间自动失败
4. **任务阻塞**: xxl-job支持阻塞处理策略（单机串行、丢弃后续调度、覆盖之前调度）
5. **日志保留**: xxl-job executor日志保留天数配置
6. **路由策略**: 支持多种路由策略（轮询、随机、故障转移、一致性哈希、分片广播、LRU等）

## 扩展功能

- [ ] 任务告警
- [ ] 任务依赖
- [ ] 任务分组
- [ ] 任务优先级
- [ ] 任务依赖检查
- [ ] 任务历史趋势分析
- [ ] 任务性能监控
- [ ] 任务重试机制

## 相关技术文档

- [xxl-job官方文档](https://www.xuxueli.com/xxl-job/)
- [MongoDB官方文档](https://www.mongodb.com/docs/)
- [Spring Data MongoDB](https://docs.spring.io/spring-data/mongodb/)

## xxl-job部署

### Docker部署（推荐）
```bash
# 启动MySQL（xxl-job需要MySQL）
docker run -d --name mysql-xxl-job \
  -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=root \
  -e MYSQL_DATABASE=xxl_job \
  -e MYSQL_USER=xxl-job \
  -e MYSQL_PASSWORD=123456 \
  mysql:8.0

# 启动xxl-job-admin
docker run -d --name xxl-job-admin \
  -p 8080:8080 \
  -e PARAMS="--spring.datasource.url=jdbc:mysql://mysql-xxl-job:3306/xxl_job?Unicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai \
  --spring.datasource.username=xxl-job \
  --spring.datasource.password=123456" \
  xuxueli/xxl-job-admin:2.4.0

# 启动Scheduler Service作为执行器
cd scheduler-service
mvn spring-boot:run
```

### 本地部署
```bash
# 1. 下载xxl-job
wget https://github.com/xuxueli/xxl-job/releases/download/2.4.0/xxl-job-2.4.0.tar.gz

# 2. 解压并启动
tar -xzf xxl-job-2.4.0.tar.gz
cd xxl-job-2.4.0

# 3. 启动xxl-job-admin
cd admin
mvn clean package -DskipTests
java -jar xxl-job-admin/target/xxl-job-admin-2.4.0.jar

# 4. 访问管理控制台
http://localhost:8080/xxl-job-admin

# 5. 创建执行器（appname=scheduler-service）
```

## 与其他服务的交互

### xxl-job Admin
- **注册执行器**: Scheduler Service自动注册为执行器
- **任务调度**: xxl-job Admin调度任务，发送到Scheduler Service
- **任务管理**: 在管理控制台配置任务

### MongoDB
- **数据存储**: 任务执行日志存储在MongoDB
- **查询**: 通过MongoDB查询任务执行历史

### 任务处理器
- **TestTaskHandler**: 测试任务
- **MailNotificationTaskHandler**: 邮件通知任务
- **statisticsJob**: 数据统计任务
- **orderTimeoutJob**: 订单超时检查任务
- **cacheCleanupJob**: 缓存清理任务
- **reportJob**: 报表生成任务

## 路由策略说明

### 轮询（ROUND）
依次轮询执行器集群中的机器执行任务，每个机器执行一次。

### 随机（RANDOM）
随机选择执行器集群中的一台机器执行任务。

### 故障转移（FAILOVER）
当第一个执行器执行失败后，尝试让下一个执行器执行。

### 一致性哈希（CONSISTENT_HASH）
使用一致性哈希算法选择执行器，保证相同任务在相同执行器上执行。

### 分片广播（SHARDING_BROADCAST)
将任务分片，多个执行器并行执行任务的不同分片。

### LRU
最近最少使用算法，淘汰长时间未执行的任务。

### FRIST
总是选择第一台可用的执行器。

## 阻塞处理策略

### 单机串行（SERIAL_EXECUTION）
同一个任务实例只能同时执行一个。

### 丢弃后续调度（DISCARD_LATER）
如果任务正在执行，丢弃后续的调度请求。

### 覆盖之前调度（COVER_EARLY）
如果任务正在执行，停止当前任务，立即执行新任务。

## Cron表达式示例

```cron
# 每天凌晨2点执行
0 0 2 * * ?

# 每小时执行
0 0 * * * ?

# 每周一上午9点执行
0 0 9 ? * MON

# 每月1号凌晨1点执行
0 0 1 1 * ?

# 每5分钟执行
0 */5 * * * ?

# 每天早上8点到晚上8点之间，每小时执行一次
0 0-20/1 * * * ?
```
