# Spring Cloud 学习系统

一个完整的 Spring Cloud 微服务学习平台，用于演示简历中涉及的 12 项主要技术。

## 技术栈

| 技术 | 用途 | 服务 |
|------------|-------|----------|
| **Spring Cloud** | 微服务架构 | 所有服务 |
| **MyBatis Plus** | ORM 框架 | 所有服务 |
| **MySQL** | 关系型数据库 | 所有服务 |
| **ShardingJDBC** | 分库分表 | 订单服务 |
| **Redis** | 缓存 | 所有服务 |
| **Redisson** | 分布式锁 | 所有服务 |
| **Kafka** | 消息队列 | 订单、消息服务 |
| **RocketMQ** | 消息队列 | 消息、订单服务 |
| **Netty** | 高性能网络通信 | 聊天服务器 |
| **MongoDB** | NoSQL 数据库 | 管理、调度服务 |
| **xxl-job** | 分布式任务调度 | 调度服务 |
| **Seata** | 分布式事务 | 分发服务 |
| **Docker** | 容器化 | 所有服务 |

## 项目状态

### 阶段 1：基础设施

**任务 1.1：项目结构与 POM 配置** - 已完成
- 已完成父 POM 及所有依赖管理
- 已完成通用父 POM（pom-versions.xml）
- 已定义项目模块

**任务 1.2：通用模块实现** - 已完成
- **common-domain**：基础实体（BaseEntityDO、BaseEntityVO）
- **common-util**：工具类（IdUtils、JsonUtils、DateUtils、StringUtils）
- **common-api**：共享 DTO 与返回结果（Result、ResultCode、PageResult、CommonErrorCode）
- **common-starter**：横切能力
  - GlobalExceptionHandler、BusinessException、RedisConfig、SwaggerConfig
  - FeignConfig、FeignInterceptor、FeignClientFactory、FeignExceptionHandler
  - BaseFeignClient、CacheHelper、LockHelper、SpringContextHolder
  - logback-spring.xml

**任务 1.3：Nacos 注册中心搭建** - 已完成
- Nacos 配置文件（application-dev.yml）

**任务 1.4：MySQL 数据库初始化** - 已完成
- init.sql：主数据库（user_db、product_db、order_db，含 4 个分片、payment_db）
- xxl-job.sql：任务调度数据库
- seata.sql：分布式事务数据库
- scripts/README.md

**任务 1.5：Redis 搭建** - 已完成（在 common-starter 中）

**任务 1.6：网关服务** - 进行中
- 网关基础设施（POM、配置、启动类）
- 用户服务（完成 100%）
  - 实体类（UserDO、UserAddressDO）
  - DTO（CreateUserRequest、UpdateUserRequest、LoginRequest、LoginResponse、UserDTO、UserInfoVO）
  - Mapper（UserMapper、UserAddressMapper）
  - 服务接口（UserService）
  - 服务实现（UserServiceImpl）
  - 控制器（UserController）
  - 安全配置（SecurityConfig）
  - JWT 工具（JwtUtils）
  - Application.yml
  - bootstrap.yml
  - README.md
- 商品服务（完成 100%）
  - 实体类（ProductDO、CategoryDO）
  - DTO（CreateProductRequest、UpdateProductRequest、ProductDTO、CategoryDTO、ProductCountDTO）
  - Mapper（ProductMapper、CategoryMapper）
  - 服务接口（ProductService）
  - 服务实现（ProductServiceImpl）
  - 控制器（ProductController）
  - 当前阶段使用数据库搜索实现
  - Application.yml
  - bootstrap.yml
  - README.md
- 订单服务（完成 100%）
  - 实体类（OrderDO）
  - DTO（CreateOrderRequest、OrderDTO、OrderQueryRequest、CancelOrderRequest）
  - Mapper（OrderMapper，含自定义查询）
  - 服务接口（OrderService）
  - 服务实现（OrderServiceImpl）
  - 控制器（OrderController）
  - Feign 客户端（ProductFeignClient、UserFeignClient）
  - Kafka 生产者（OrderKafkaProducer）
  - RocketMQ 生产者（OrderRocketMQProducer）
  - 事件（OrderEvent）
  - Application.yml
  - bootstrap.yml
  - README.md
- 支付服务（完成 100%）
- 消息服务（完成 100%）
- 分发服务（最小可用一版已完成）
- 调度服务（完成 100%）
- 管理服务（完成 100%）
  - MongoDB 文档模型
  - DTO（写入、查询、响应、统计）
  - Repository
  - 服务（操作日志、审计日志、错误日志、统计）
  - 控制器
  - README.md
- 聊天服务器（完成 100%）

## 项目结构

```
spring-cloud-learning-system/
├── common/                          # 公共模块，已完成
│   ├── pom.xml
│   ├── common-api/                  # 通用 API 定义，已完成
│   ├── common-starter/              # 通用启动器，已完成
│   ├── common-util/                 # 通用工具，已完成
│   └── common-domain/               # 领域模型，已完成
├── gateway/                         # 网关服务，待完成
│   └── gateway-service/             # Spring Cloud Gateway，待完成
├── user-service/                    # 用户服务，已完成 100%
│   ├── README.md                    # 服务文档，已完成
│   ├── pom.xml
│   ├── src/main/java/com/learning/user/
│   │   ├── UserServiceApplication.java
│   │   ├── config/SecurityConfig.java
│   │   ├── controller/UserController.java
│   │   ├── dto/
│   │   ├── entity/UserDO.java
│   │   ├── entity/UserAddressDO.java
│   │   ├── mapper/UserMapper.java
│   │   ├── mapper/UserAddressMapper.java
│   │   ├── service/UserService.java
│   │   ├── service/impl/UserServiceImpl.java
│   │   └── util/JwtUtils.java
│   └── src/main/resources/
│       ├── application.yml
│       └── bootstrap.yml
├── product-service/                 # 商品服务，已完成 100%
│   ├── README.md
│   ├── pom.xml
│   ├── src/main/java/com/learning/product/
│   │   ├── ProductServiceApplication.java
│   │   ├── config/
│   │   ├── controller/ProductController.java
│   │   ├── dto/
│   │   ├── entity/ProductDO.java
│   │   ├── entity/CategoryDO.java
│   │   ├── mapper/ProductMapper.java
│   │   ├── mapper/CategoryMapper.java
│   │   ├── service/ProductService.java
│   │   ├── service/impl/ProductServiceImpl.java
│   │   └── repository/
│   └── src/main/resources/
│       ├── application.yml
│       └── bootstrap.yml
├── order-service/                   # 订单服务，已完成 100%
│   ├── README.md
│   ├── pom.xml
│   ├── src/main/java/com/learning/order/
│   │   ├── OrderServiceApplication.java
│   │   ├── controller/OrderController.java
│   │   ├── dto/
│   │   ├── entity/OrderDO.java
│   │   ├── feign/ProductFeignClient.java
│   │   ├── feign/UserFeignClient.java
│   │   ├── event/OrderEvent.java
│   │   ├── mapper/OrderMapper.java
│   │   ├── producer/OrderKafkaProducer.java
│   │   ├── producer/OrderRocketMQProducer.java
│   │   ├── service/OrderService.java
│   │   └── service/impl/OrderServiceImpl.java
│   ├── resources/mapper/OrderMapper.xml
│   └── src/main/resources/
│       ├── application.yml
│       └── bootstrap.yml
├── payment-service/                 # 支付服务，已完成 100%
├── message-service/                 # 消息服务，已完成 100%
├── distribution-service/            # 分发服务，最小可用一版已完成
├── scheduler-service/               # 调度服务，已完成 100%
├── admin-service/                   # 管理服务，已完成 100%
├── chat-server/                     # 聊天服务器，已完成
├── scripts/                         # 脚本文件，已完成
│   ├── README.md
│   └── mysql/
│       ├── init.sql                 # 主数据库初始化
│       ├── xxl-job.sql              # xxl-job 数据库初始化
│       └── seata.sql                # Seata 数据库初始化
├── config/                          # 配置文件
│   └── nacos/application-dev.yml    # Nacos 配置
├── docker/                          # Docker 配置，待完成
├── docs/                            # 文档
│   ├── specs/
│   │   ├── 2026-05-30-design.md     # 设计文档
│   │   └── 2026-06-01-plan.md       # 实施计划
│   └── database/                    # 数据库文档，待完成
└── pom.xml                          # 父 POM
```

## 数据库概览

### learning_system（主库）
- **user_db**：用户信息（t_user_info、t_user_address）
- **product_db**：商品（t_product、t_category）
- **order_db**：订单，使用 ShardingJDBC（t_order_0、t_order_1、t_order_2、t_order_3）
- **payment_db**：支付与退款（t_payment、t_refund）

### xxl-job
- 任务管理：xxl_job_info
- 执行日志：xxl_job_log
- 执行器管理：xxl_job_group
- 用户账号：xxl_job_user（admin/123456）

### seata
- 全局事务：global_table
- 分支事务：branch_table
- 全局锁：lock_table

## 当前进度

### 阶段 1：基础设施
- 1.1：POM 配置 - 100%
- 1.2：通用模块 - 100%
- 1.3：Nacos 搭建 - 100%
- 1.4：MySQL 初始化 - 100%
- 1.5：Redis 搭建 - 100%
- 1.6：网关服务 - 30%

### 阶段 2：核心服务
- 用户服务 - 100%
- 商品服务 - 100%
- 订单服务 - 100%
- 支付服务 - 100%
- 消息服务 - 100%
- 调度服务 - 100%
- 分发服务 - 最小可用一版已完成

### 阶段 3：高级功能
- 管理服务（MongoDB 日志）
- 聊天服务器（Netty）
  - Netty 启动配置
  - 数据包模型
  - 编解码器
  - 会话管理器
  - 路由服务
  - 处理器
  - 协议文档
  - 测试文档
  - TCP 测试脚本
  - README.md

## 快速开始

### 前置条件

- Java 17+
- Maven 3.8+
- Docker 和 Docker Compose
- MySQL 8.0
- Redis 7.0
- MongoDB 6.0+

### 步骤 1：初始化数据库

```powershell
# PowerShell
mysql -h localhost -u root -proot -e "source D:/coding_file/view/scripts/mysql/init.sql"
mysql -h localhost -u root -proot -e "source D:/coding_file/view/scripts/mysql/xxl-job.sql"
mysql -h localhost -u root -proot -e "source D:/coding_file/view/scripts/mysql/seata.sql"
```

### 步骤 2：构建公共模块

```bash
cd D:\coding_file\view
mvn clean install
```

### 步骤 3：启动 Nacos（Docker）

```bash
docker-compose -f docker/docker-compose.yml up -d nacos
```

### 步骤 4：启动服务

```bash
# 启动用户服务
cd user-service
mvn spring-boot:run

# 启动商品服务
cd ../product-service
mvn spring-boot:run

# 启动订单服务
cd ../order-service
mvn spring-boot:run

# 启动支付服务
cd ../payment-service
mvn spring-boot:run

# 启动消息服务
cd ../message-service
mvn spring-boot:run

# 启动调度服务
cd ../scheduler-service
mvn spring-boot:run

# 启动管理服务
cd ../admin-service
mvn spring-boot:run

# 启动聊天服务器
cd ../chat-server
mvn spring-boot:run
```

### 步骤 5：测试服务

#### 用户服务

```bash
# 注册
curl -X POST http://localhost:8082/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123", "phone": "13800138000", "email": "test@example.com"}'

# 登录
curl -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'

# 获取用户列表
curl -X GET "http://localhost:8082/api/user/list?current=1&size=10"
```

#### 商品服务

```bash
# 创建商品
curl -X POST http://localhost:8083/api/product/create \
  -H "Content-Type: application/json" \
  -d '{"categoryId": 1, "productName": "测试商品", "unitPrice": 99.00, "stock": 100}'

# 搜索商品
curl -X GET "http://localhost:8083/api/product/search?keyword=商品&current=1&size=10"

# 获取分类
curl -X GET http://localhost:8083/api/product/categories

# 获取热销商品
curl -X GET "http://localhost:8083/api/product/top-sales?limit=5"
```

#### 聊天服务器
```bash
# 启动 Chat Server
mvn -pl chat-server spring-boot:run

# 在项目根目录执行 TCP 测试脚本
python chat-server/scripts/chat_test_client.py
```

更详细的协议、测试用例和预期行为见：
- `chat-server/PROTOCOL.md`
- `chat-server/TESTING.md`
- `chat-server/TCP-TEST-SCRIPT-GUIDE.md`

#### 其他服务
各服务的详细调用示例请查看对应 README：
- `user-service/README.md`
- `product-service/README.md`
- `order-service/README.md`
- `payment-service/README.md`
- `message-service/README.md`
- `admin-service/README.md`


## 文档

- [数据库脚本](scripts/README.md)
- [Ubuntu 服务器部署指南](docs/Ubuntu-Deployment.md)
- [Ubuntu 服务器部署指南（中文版）](docs/Ubuntu-Deployment-CN.md)
- [Ubuntu 部署简版 Runbook（中文版）](docs/Ubuntu-Deployment-Runbook-CN.md)
- [Win10 + WSL2 分发 / Seata 最短执行清单（中文版）](docs/Win10-WSL2-Distribution-Seata-Runbook-CN.md)
- [未来功能索引](docs/future-features/README.md)
- [阶段进展总结](docs/Progress-Summary.md)
- [编译成功后的下一步](docs/Next-Steps-After-Compile-Success.md)
- [用户服务 README](user-service/README.md)
- [商品服务 README](product-service/README.md)
- [订单服务 README](order-service/README.md)
- [支付服务 README](payment-service/README.md)
- [消息服务 README](message-service/README.md)
- [分发服务 README](distribution-service/README.md)
- [分发服务测试说明](distribution-service/TESTING.md)
- [管理服务 README](admin-service/README.md)
- [聊天服务器 README](chat-server/README.md)

## 通用 API 用法

### Result 包装器

```java
// 成功响应
return Result.success(data);

// 带自定义消息的成功响应
return Result.success("Operation successful", data);

// 错误响应
return Result.fail("Operation failed");

// 自定义错误响应
return Result.fail(400, "Invalid parameter");
```

### PageResult

```java
// 创建分页结果
PageResult<UserVO> pageResult = PageResult.of(users, total, currentPage, pageSize);
return Result.success(pageResult);
```

### 通用错误码

```java
throw new BusinessException(CommonErrorCode.USER_NOT_FOUND);
throw new BusinessException(CommonErrorCode.BALANCE_NOT_ENOUGH);
```

### Redis 工具

```java
@Autowired
private CacheHelper cacheHelper;

// 设置带 TTL 的缓存
cacheHelper.set("key", value, 3600); // 1 小时

// 获取缓存
User user = cacheHelper.get("key");

// 分布式锁
@Autowired
private LockHelper lockHelper;

lockHelper.executeWithLock("order:" + orderId, () -> {
    // 处理订单
});
```

## 许可证

MIT License
