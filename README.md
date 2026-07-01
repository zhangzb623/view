# Spring Cloud Learning System

A comprehensive Spring Cloud microservices learning platform demonstrating all 12 major technologies from the resume.

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

## Project Status

### Phase 1: Basic Infrastructure

**Task 1.1: Project Structure & POM Configuration** - ✅ COMPLETED
- ✅ Parent POM with all dependency management
- ✅ Common parent POM (pom-versions.xml)
- ✅ Project modules defined

**Task 1.2: Common Module Implementation** - ✅ COMPLETED
- ✅ **common-domain**: Base entities (BaseEntityDO, BaseEntityVO)
- ✅ **common-util**: Utility classes (IdUtils, JsonUtils, DateUtils, StringUtils)
- ✅ **common-api**: Shared DTOs and results (Result, ResultCode, PageResult, CommonErrorCode)
- ✅ **common-starter**: Cross-cutting concerns
  - GlobalExceptionHandler, BusinessException, RedisConfig, SwaggerConfig
  - FeignConfig, FeignInterceptor, FeignClientFactory, FeignExceptionHandler
  - BaseFeignClient, CacheHelper, LockHelper, SpringContextHolder
  - logback-spring.xml

**Task 1.3: Nacos Registration Center Setup** - ✅ COMPLETED
- ✅ Nacos config files (application-dev.yml)

**Task 1.4: MySQL Database Initialization** - ✅ COMPLETED
- ✅ init.sql - Main database (user_db, product_db, order_db with 4 shards, payment_db)
- ✅ xxl-job.sql - Task scheduling database
- ✅ seata.sql - Distributed transaction database
- ✅ scripts/README.md

**Task 1.5: Redis Setup** - ✅ COMPLETED (in common-starter)

**Task 1.6: Gateway Service** - 🔄 IN PROGRESS
- ✅ Gateway infrastructure (POM, config, application class)
- ✅ User Service (100% complete)
  - ✅ Entity classes (UserDO, UserAddressDO)
  - ✅ DTOs (CreateUserRequest, UpdateUserRequest, LoginRequest, LoginResponse, UserDTO, UserInfoVO)
  - ✅ Mappers (UserMapper, UserAddressMapper)
  - ✅ Service interface (UserService)
  - ✅ Service implementation (UserServiceImpl)
  - ✅ Controller (UserController)
  - ✅ Security config (SecurityConfig)
  - ✅ JWT Utils (JwtUtils)
  - ✅ Application.yml
  - ✅ bootstrap.yml
  - ✅ README.md
- ✅ Product Service (100% complete)
  - ✅ Entity classes (ProductDO, CategoryDO)
  - ✅ DTOs (CreateProductRequest, UpdateProductRequest, ProductDTO, CategoryDTO, ProductCountDTO)
  - ✅ Mappers (ProductMapper, CategoryMapper)
  - ✅ Service interface (ProductService)
  - ✅ Service implementation (ProductServiceImpl)
  - ✅ Controller (ProductController)
  - ✅ Database-backed search implementation
  - ✅ Application.yml
  - ✅ bootstrap.yml
  - ✅ README.md
- ✅ Order Service (100% complete)
  - ✅ Entity classes (OrderDO)
  - ✅ DTOs (CreateOrderRequest, OrderDTO, OrderQueryRequest, CancelOrderRequest)
  - ✅ Mappers (OrderMapper with custom queries)
  - ✅ Service interface (OrderService)
  - ✅ Service implementation (OrderServiceImpl)
  - ✅ Controller (OrderController)
  - ✅ Feign Clients (ProductFeignClient, UserFeignClient)
  - ✅ Kafka Producer (OrderKafkaProducer)
  - ✅ RocketMQ Producer (OrderRocketMQProducer)
  - ✅ Event (OrderEvent)
  - ✅ Application.yml
  - ✅ bootstrap.yml
  - ✅ README.md
- ✅ Payment Service (100% complete)
- ✅ Message Service (100% complete)
- ✅ Distribution Service (minimal first slice complete)
- ✅ Scheduler Service (100% complete)
- ✅ Admin Service (100% complete)
  - ✅ MongoDB document models
  - ✅ DTOs (write/query/response/statistics)
  - ✅ Repositories
  - ✅ Services (operation/audit/error/statistics)
  - ✅ Controllers
  - ✅ README.md
- ✅ Chat Server (100% complete)

## Project Structure

```
spring-cloud-learning-system/
├── common/                          # 公共模块 ✅
│   ├── pom.xml
│   ├── common-api/                  # 通用API定义 ✅
│   ├── common-starter/              # 通用启动器 ✅
│   ├── common-util/                 # 通用工具 ✅
│   └── common-domain/               # 领域模型 ✅
├── gateway/                         # 网关服务 ⬜
│   └── gateway-service/             # Spring Cloud Gateway ⬜
├── user-service/                    # User service ✅ 100%
│   ├── README.md                    # 服务文档 ✅
│   ├── pom.xml
│   ├── src/main/java/com/learning/user/
│   │   ├── UserServiceApplication.java ✅
│   │   ├── config/
│   │   │   └── SecurityConfig.java ✅
│   │   ├── controller/
│   │   │   └── UserController.java ✅
│   │   ├── dto/
│   │   ├── entity/
│   │   │   ├── UserDO.java ✅
│   │   │   └── UserAddressDO.java ✅
│   │   ├── mapper/
│   │   │   ├── UserMapper.java ✅
│   │   │   └── UserAddressMapper.java ✅
│   │   ├── service/
│   │   │   ├── UserService.java ✅
│   │   │   └── impl/
│   │   │       └── UserServiceImpl.java ✅
│   │   └── util/
│   │       └── JwtUtils.java ✅
│   └── src/main/resources/
│       ├── application.yml ✅
│       └── bootstrap.yml ✅
├── product-service/                 # 商品服务 ✅ 100%
│   ├── README.md                    # 服务文档 ✅
│   ├── pom.xml
│   ├── src/main/java/com/learning/product/
│   │   ├── ProductServiceApplication.java ✅
│   │   ├── config/
│   │   ├── controller/
│   │   │   └── ProductController.java ✅
│   │   ├── dto/
│   │   ├── entity/
│   │   │   ├── ProductDO.java ✅
│   │   │   └── CategoryDO.java ✅
│   │   ├── mapper/
│   │   │   ├── ProductMapper.java ✅
│   │   │   └── CategoryMapper.java ✅
│   │   ├── service/
│   │   │   ├── ProductService.java ✅
│   │   │   └── impl/
│   │   │       └── ProductServiceImpl.java ✅
│   │   └── repository/
│   │       └── (search implementation is database-backed in the current phase)
│   └── src/main/resources/
│       ├── application.yml ✅
│       └── bootstrap.yml ✅
├── order-service/                   # 订单服务 ✅ 100%
│   ├── README.md                    # 服务文档 ✅
│   ├── pom.xml
│   ├── src/main/java/com/learning/order/
│   │   ├── OrderServiceApplication.java ✅
│   │   ├── controller/
│   │   │   └── OrderController.java ✅
│   │   ├── dto/
│   │   │   ├── CreateOrderRequest.java ✅
│   │   │   ├── OrderDTO.java ✅
│   │   │   ├── OrderQueryRequest.java ✅
│   │   │   └── CancelOrderRequest.java ✅
│   │   ├── entity/
│   │   │   └── OrderDO.java ✅
│   │   ├── feign/
│   │   │   ├── ProductFeignClient.java ✅
│   │   │   └── UserFeignClient.java ✅
│   │   ├── event/
│   │   │   └── OrderEvent.java ✅
│   │   ├── mapper/
│   │   │   └── OrderMapper.java ✅
│   │   ├── producer/
│   │   │   ├── OrderKafkaProducer.java ✅
│   │   │   └── OrderRocketMQProducer.java ✅
│   │   ├── service/
│   │   │   ├── OrderService.java ✅
│   │   │   └── impl/
│   │   │       └── OrderServiceImpl.java ✅
│   │   └── resources/
│   │       └── mapper/
│   │           └── OrderMapper.xml ✅
│   └── src/main/resources/
│       ├── application.yml ✅
│       └── bootstrap.yml ✅
├── payment-service/                 # 支付服务 ✅ 100%
│   ├── README.md                    # 服务文档 ✅
│   ├── pom.xml
│   ├── src/main/java/com/learning/payment/
│   │   ├── PaymentServiceApplication.java ✅
│   │   ├── controller/
│   │   │   └── PaymentController.java ✅
│   │   ├── dto/
│   │   │   ├── CreatePaymentRequest.java ✅
│   │   │   ├── PaymentDTO.java ✅
│   │   │   ├── RefundDTO.java ✅
│   │   │   ├── RefundRequest.java ✅
│   │   │   └── PaymentStatusRequest.java ✅
│   │   ├── entity/
│   │   │   ├── PaymentDO.java ✅
│   │   │   └── RefundDO.java ✅
│   │   ├── feign/
│   │   │   └── OrderFeignClient.java ✅
│   │   ├── mapper/
│   │   │   ├── PaymentMapper.java ✅
│   │   │   └── RefundMapper.java ✅
│   │   ├── service/
│   │   │   ├── PaymentService.java ✅
│   │   │   ├── RefundService.java ✅
│   │   │   └── impl/
│   │   │       ├── PaymentServiceImpl.java ✅
│   │   │       └── RefundServiceImpl.java ✅
│   │   └── resources/
│   │       └── mapper/
│   │           ├── PaymentMapper.xml ✅
│   │           └── RefundMapper.xml ✅
│   └── src/main/resources/
│       ├── application.yml ✅
│       └── bootstrap.yml ✅
├── message-service/                 # Message service ✅ 100%
├── distribution-service/            # Distribution service ✅ first slice
├── scheduler-service/               # Scheduler service ✅ 100%
├── admin-service/                   # 管理服务 ✅ 100%
│   ├── README.md                    # 服务文档 ✅
│   ├── pom.xml
│   ├── src/main/java/com/learning/admin/
│   │   ├── AdminServiceApplication.java ✅
│   │   ├── controller/
│   │   │   ├── OperationLogController.java ✅
│   │   │   ├── AuditLogController.java ✅
│   │   │   ├── ErrorLogController.java ✅
│   │   │   └── StatisticsController.java ✅
│   │   ├── document/
│   │   │   ├── OperationLogDO.java ✅
│   │   │   ├── AuditLogDO.java ✅
│   │   │   └── ErrorLogDO.java ✅
│   │   ├── dto/
│   │   │   ├── CreateOperationLogRequest.java ✅
│   │   │   ├── CreateAuditLogRequest.java ✅
│   │   │   ├── CreateErrorLogRequest.java ✅
│   │   │   ├── OperationLogQueryRequest.java ✅
│   │   │   ├── AuditLogQueryRequest.java ✅
│   │   │   ├── ErrorLogQueryRequest.java ✅
│   │   │   ├── OperationLogDTO.java ✅
│   │   │   ├── AuditLogDTO.java ✅
│   │   │   ├── ErrorLogDTO.java ✅
│   │   │   ├── LogOverviewStatisticsDTO.java ✅
│   │   │   ├── ErrorTrendPointDTO.java ✅
│   │   │   └── ServiceLogRankDTO.java ✅
│   │   ├── repository/
│   │   │   ├── OperationLogRepository.java ✅
│   │   │   ├── AuditLogRepository.java ✅
│   │   │   └── ErrorLogRepository.java ✅
│   │   └── service/
│   │       ├── OperationLogService.java ✅
│   │       ├── AuditLogService.java ✅
│   │       ├── ErrorLogService.java ✅
│   │       ├── StatisticsService.java ✅
│   │       └── impl/
│   │           ├── OperationLogServiceImpl.java ✅
│   │           ├── AuditLogServiceImpl.java ✅
│   │           ├── ErrorLogServiceImpl.java ✅
│   │           └── StatisticsServiceImpl.java ✅
│   └── src/main/resources/
│       ├── application.yml ✅
│       └── bootstrap.yml ✅
├── chat-server/                     # 聊天服务器 ✅
├── scripts/                         # 脚本文件 ✅
│   ├── README.md                    # 脚本说明 ✅
│   └── mysql/                       # MySQL脚本 ✅
│       ├── init.sql                 # 主数据库初始化 ✅
│       ├── xxl-job.sql              # xxl-job数据库初始化 ✅
│       └── seata.sql                # Seata数据库初始化 ✅
├── config/                          # 配置文件 ✅
│   └── nacos/
│       └── application-dev.yml      # Nacos配置 ✅
├── docker/                          # Docker配置 ⬜
├── docs/                            # 文档 ✅
│   ├── specs/
│   │   ├── 2026-05-30-design.md     # 设计文档 ✅
│   │   └── 2026-06-01-plan.md       # 实施计划 ✅
│   └── database/                    # 数据库文档 ⬜
└── pom.xml                          # 父POM ✅
```

## Database Summary

### learning_system (Main)
- **user_db**: User info (t_user_info, t_user_address)
- **product_db**: Product (t_product, t_category)
- **order_db**: Orders with ShardingJDBC (t_order_0, t_order_1, t_order_2, t_order_3)
- **payment_db**: Payments and refunds (t_payment, t_refund)

### xxl-job
- Task management: xxl_job_info
- Execution logs: xxl_job_log
- Executor management: xxl_job_group
- User accounts: xxl_job_user (admin/123456)

### seata
- Global transactions: global_table
- Branch transactions: branch_table
- Global locks: lock_table

## Current Progress

### Phase 1: Basic Infrastructure
- ✅ 1.1: POM Configuration - 100%
- ✅ 1.2: Common Module - 100%
- ✅ 1.3: Nacos Setup - 100%
- ✅ 1.4: MySQL Initialization - 100%
- ✅ 1.5: Redis Setup - 100%
- 🔄 1.6: Gateway Service - 30%

### Phase 2: Core Services
- ✅ User Service - 100%
- ✅ Product Service - 100%
- ✅ Order Service - 100%
- ✅ Payment Service - 100%
- ✅ Message Service - 100%
- ✅ Scheduler Service - 100%
- ✅ Distribution Service - first slice complete

### Phase 3: Advanced Features
- ✅ Admin service (MongoDB logs)
- ✅ Chat server (Netty)
  - ✅ Netty bootstrap
  - ✅ packet models
  - ✅ codec
  - ✅ session manager
  - ✅ routing service
  - ✅ handlers
  - ✅ protocol docs
  - ✅ testing docs
  - ✅ TCP test script
  - ✅ README.md

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- MySQL 8.0
- Redis 7.0
- MongoDB 6.0+

### Step 1: Initialize Databases

```powershell
# PowerShell
mysql -h localhost -u root -proot -e "source D:/coding_file/view/scripts/mysql/init.sql"
mysql -h localhost -u root -proot -e "source D:/coding_file/view/scripts/mysql/xxl-job.sql"
mysql -h localhost -u root -proot -e "source D:/coding_file/view/scripts/mysql/seata.sql"
```

### Step 2: Build Common Module

```bash
cd D:\coding_file\view
mvn clean install
```

### Step 3: Start Nacos (Docker)

```bash
docker-compose -f docker/docker-compose.yml up -d nacos
```

### Step 4: Start Services

```bash
# Start User Service
cd user-service
mvn spring-boot:run

# Start Product Service
cd ../product-service
mvn spring-boot:run

# Start Order Service
cd ../order-service
mvn spring-boot:run

# Start Payment Service
cd ../payment-service
mvn spring-boot:run

# Start Message Service
cd ../message-service
mvn spring-boot:run

# Start Scheduler Service
cd ../scheduler-service
mvn spring-boot:run

# Start Admin Service
cd ../admin-service
mvn spring-boot:run

# Start Chat Server
cd ../chat-server
mvn spring-boot:run
```

### Step 5: Test Services

#### User Service
```bash
# Register
curl -X POST http://localhost:8082/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123", "phone": "13800138000", "email": "test@example.com"}'

# Login
curl -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser", "password": "password123"}'

# Get User List
curl -X GET "http://localhost:8082/api/user/list?current=1&size=10"
```

#### Product Service
```bash
# Create Product
curl -X POST http://localhost:8083/api/product/create \
  -H "Content-Type: application/json" \
  -d '{"categoryId": 1, "productName": "测试商品", "unitPrice": 99.00, "stock": 100}'

# Search Products
curl -X GET "http://localhost:8083/api/product/search?keyword=商品&current=1&size=10"

# Get Categories
curl -X GET http://localhost:8083/api/product/categories

# Get Top Sales
curl -X GET "http://localhost:8083/api/product/top-sales?limit=5"
```

#### Chat Server
```bash
# Start Chat Server
mvn -pl chat-server spring-boot:run

# Run TCP test script from project root
python chat-server/scripts/chat_test_client.py
```

For detailed protocol, test cases, and expected behavior, see:
- `chat-server/PROTOCOL.md`
- `chat-server/TESTING.md`
- `chat-server/TCP-TEST-SCRIPT-GUIDE.md`

#### Other Services
Detailed request examples are maintained in each service README:
- `user-service/README.md`
- `product-service/README.md`
- `order-service/README.md`
- `payment-service/README.md`
- `message-service/README.md`
- `admin-service/README.md`


#### Chat Server
```bash
# Start Chat Server
mvn -pl chat-server spring-boot:run

# Run TCP test script from project root
python chat-server/scripts/chat_test_client.py
```

For detailed protocol, test cases, and expected behavior, see:
- `chat-server/PROTOCOL.md`
- `chat-server/TESTING.md`
- `chat-server/TCP-TEST-SCRIPT-GUIDE.md`

#### Other Services
Detailed request examples are maintained in each service README:
- `user-service/README.md`
- `product-service/README.md`
- `order-service/README.md`
- `payment-service/README.md`
- `message-service/README.md`
- `admin-service/README.md`


## Documentation

- [Database Scripts](scripts/README.md)
- [Ubuntu Server Deployment Guide](docs/Ubuntu-Deployment.md)
- [Ubuntu 服务器部署指南（中文版）](docs/Ubuntu-Deployment-CN.md)
- [Ubuntu 部署简版 Runbook（中文版）](docs/Ubuntu-Deployment-Runbook-CN.md)
- [Future Features Index](docs/future-features/README.md)
- [Progress Summary](docs/Progress-Summary.md)
- [Next Steps After Compile Success](docs/Next-Steps-After-Compile-Success.md)
- [User Service README](user-service/README.md)
- [Product Service README](product-service/README.md)
- [Order Service README](order-service/README.md)
- [Payment Service README](payment-service/README.md)
- [Message Service README](message-service/README.md)
- [Distribution Service README](distribution-service/README.md)
- [Distribution Service Testing Guide](distribution-service/TESTING.md)
- [Admin Service README](admin-service/README.md)
- [Chat Server README](chat-server/README.md)

## Common API Usage

### Result Wrapper

```java
// Success response
return Result.success(data);

// Success response with custom message
return Result.success("Operation successful", data);

// Error response
return Result.fail("Operation failed");

// Custom error response
return Result.fail(400, "Invalid parameter");
```

### PageResult

```java
// Create paginated result
PageResult<UserVO> pageResult = PageResult.of(users, total, currentPage, pageSize);
return Result.success(pageResult);
```

### Common Error Codes

```java
throw new BusinessException(CommonErrorCode.USER_NOT_FOUND);
throw new BusinessException(CommonErrorCode.BALANCE_NOT_ENOUGH);
```

### Redis Utilities

```java
@Autowired
private CacheHelper cacheHelper;

// Set cache with TTL
cacheHelper.set("key", value, 3600); // 1 hour

// Get cache
User user = cacheHelper.get("key");

// Distributed lock
@Autowired
private LockHelper lockHelper;

lockHelper.executeWithLock("order:" + orderId, () -> {
    // Process order
});
```

## License

MIT License
