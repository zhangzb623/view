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
- ✅ User Service (80% complete)
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
  - ✅ Elasticsearch integration
  - ✅ Application.yml
  - ✅ bootstrap.yml
  - ✅ README.md
- 🔄 Order Service (100% complete)
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
- ⬜ Payment Service (0%)
- ⬜ Message Service (0%)
- ⬜ Distribution Service (0%)
- ⬜ Scheduler Service (0%)
- ✅ Admin Service (100% complete)
  - ✅ MongoDB document models
  - ✅ DTOs (write/query/response/statistics)
  - ✅ Repositories
  - ✅ Services (operation/audit/error/statistics)
  - ✅ Controllers
  - ✅ README.md
- ⬜ Chat Server (0%)

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
├── user-service/                    # 用户服务 🔄 80%
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
│   │       └── (Elasticsearch repositories will be added)
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
├── message-service/                 # 消息服务 ⬜
├── distribution-service/            # 分发服务 ⬜
├── scheduler-service/               # 调度服务 ⬜
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
- ✅ User Service - 80%
- ✅ Product Service - 100%
- ✅ Order Service - 100%
- ✅ Payment Service - 100%
- ✅ Message Service - 100%
- ✅ Scheduler Service - 100%

### Phase 3: Advanced Features
- ✅ Admin service (MongoDB logs)
- ✅ Chat server (Netty)
  - ✅ Netty bootstrap
  - ✅ packet models
  - ✅ codec
  - ✅ session manager
  - ✅ routing service
  - ✅ handlers
  - ✅ README.md

## Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker & Docker Compose
- MySQL 8.0
- Redis 7.0
- Elasticsearch 7.10+ (for Product Service search)

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

# Start Admin Service
cd ../admin-service
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

#### Order Service
```bash
# Create Order
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

# Pay Order
curl -X POST "http://localhost:8084/api/order/1/pay?transactionId=TXN123&paymentMethod=3"

# Get Order
curl -X GET http://localhost:8084/api/order/1

# Cancel Order
curl -X POST http://localhost:8084/api/order/cancel \
  -H "Content-Type: application/json" \
  -d '{"orderId": 1, "cancelReason": "不想要了"}'

# Get Order List
curl -X GET "http://localhost:8084/api/order/user/list?userId=1&page=1&size=10"

# Ship Order
curl -X POST "http://localhost:8084/api/order/1/ship?trackingNumber=SF123456789"

# Complete Order
curl -X POST http://localhost:8084/api/order/1/complete

# Payment Service
```bash
# Create Payment
curl -X POST http://localhost:8085/api/payment/create \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "userId": 1,
    "paymentMethod": 3,
    "amount": 7999.00
  }'

# Call Third Party Payment
curl -X POST http://localhost:8085/api/payment/1/pay

# Get Payment
curl -X GET http://localhost:8085/api/payment/1

# Create Refund
curl -X POST http://localhost:8085/api/payment/refund/create \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": 1,
    "refundAmount": 7999.00,
    "refundReason": "不想要了"
  }'

# Message Service
```bash
# Create Message
curl -X POST http://localhost:8086/api/message/create \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "messageType": 1,
    "title": "订单通知",
    "content": "您的订单已创建",
    "important": 1,
    "businessId": "1001",
    "businessType": "order",
    "source": 1
  }'

# Get Message
curl -X GET http://localhost:8086/api/message/1

# Get User Messages
curl -X GET "http://localhost:8086/api/message/user/list?userId=1&page=1&size=10"

# Mark as Read
curl -X POST http://localhost:8086/api/message/1/read

# Count Unread
curl -X GET http://localhost:8086/api/message/user/1/unread/count
```

#### Admin Service
```bash
# Write Operation Log
curl -X POST http://localhost:8088/api/admin/logs/operation \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "order-service",
    "operatorId": 1,
    "operatorName": "system",
    "operationType": "CREATE_ORDER",
    "businessType": "order",
    "businessId": "1001",
    "requestPath": "/api/order/create",
    "requestMethod": "POST",
    "resultStatus": 200,
    "resultMessage": "success",
    "ip": "127.0.0.1"
  }'

# Write Audit Log
curl -X POST http://localhost:8088/api/admin/logs/audit \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "payment-service",
    "businessType": "payment",
    "businessId": "2001",
    "beforeStatus": "PROCESSING",
    "afterStatus": "SUCCESS",
    "action": "PAYMENT_CALLBACK",
    "reason": "gateway callback",
    "operatorId": 1,
    "traceId": "trace-001"
  }'

# Write Error Log
curl -X POST http://localhost:8088/api/admin/logs/error \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "scheduler-service",
    "businessType": "task",
    "businessId": "job-001",
    "errorCode": "TASK_EXEC_FAIL",
    "errorMessage": "job execution failed",
    "stackSummary": "java.lang.RuntimeException: job execution failed",
    "traceId": "trace-002",
    "severity": "HIGH"
  }'

# Get Overview Statistics
curl -X GET http://localhost:8088/api/admin/logs/statistics/overview
```

#### Order Service
```bash
# Create Order
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

# Pay Order
curl -X POST "http://localhost:8084/api/order/1/pay?transactionId=TXN123&paymentMethod=3"

# Get Order
curl -X GET http://localhost:8084/api/order/1

# Cancel Order
curl -X POST http://localhost:8084/api/order/cancel \
  -H "Content-Type: application/json" \
  -d '{"orderId": 1, "cancelReason": "不想要了"}'

# Get Order List
curl -X GET "http://localhost:8084/api/order/user/list?userId=1&page=1&size=10"

# Ship Order
curl -X POST "http://localhost:8084/api/order/1/ship?trackingNumber=SF123456789"

# Complete Order
curl -X POST http://localhost:8084/api/order/1/complete

# Payment Service
```bash
# Create Payment
curl -X POST http://localhost:8085/api/payment/create \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": 1,
    "userId": 1,
    "paymentMethod": 3,
    "amount": 7999.00
  }'

# Call Third Party Payment
curl -X POST http://localhost:8085/api/payment/1/pay

# Get Payment
curl -X GET http://localhost:8085/api/payment/1

# Create Refund
curl -X POST http://localhost:8085/api/payment/refund/create \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": 1,
    "refundAmount": 7999.00,
    "refundReason": "不想要了"
  }'

# Message Service
```bash
# Create Message
curl -X POST http://localhost:8086/api/message/create \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "messageType": 1,
    "title": "订单通知",
    "content": "您的订单已创建",
    "important": 1,
    "businessId": "1001",
    "businessType": "order",
    "source": 1
  }'

# Get Message
curl -X GET http://localhost:8086/api/message/1

# Get User Messages
curl -X GET "http://localhost:8086/api/message/user/list?userId=1&page=1&size=10"

# Mark as Read
curl -X POST http://localhost:8086/api/message/1/read

# Count Unread
curl -X GET http://localhost:8086/api/message/user/1/unread/count
```

#### Admin Service
```bash
# Write Operation Log
curl -X POST http://localhost:8088/api/admin/logs/operation \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "order-service",
    "operatorId": 1,
    "operatorName": "system",
    "operationType": "CREATE_ORDER",
    "businessType": "order",
    "businessId": "1001",
    "requestPath": "/api/order/create",
    "requestMethod": "POST",
    "resultStatus": 200,
    "resultMessage": "success",
    "ip": "127.0.0.1"
  }'

# Write Audit Log
curl -X POST http://localhost:8088/api/admin/logs/audit \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "payment-service",
    "businessType": "payment",
    "businessId": "2001",
    "beforeStatus": "PROCESSING",
    "afterStatus": "SUCCESS",
    "action": "PAYMENT_CALLBACK",
    "reason": "gateway callback",
    "operatorId": 1,
    "traceId": "trace-001"
  }'

# Write Error Log
curl -X POST http://localhost:8088/api/admin/logs/error \
  -H "Content-Type: application/json" \
  -d '{
    "serviceName": "scheduler-service",
    "businessType": "task",
    "businessId": "job-001",
    "errorCode": "TASK_EXEC_FAIL",
    "errorMessage": "job execution failed",
    "stackSummary": "java.lang.RuntimeException: job execution failed",
    "traceId": "trace-002",
    "severity": "HIGH"
  }'

# Get Overview Statistics
curl -X GET http://localhost:8088/api/admin/logs/statistics/overview
```

## Documentation

- [Database Scripts](scripts/README.md)
- [Ubuntu Server Deployment Guide](docs/Ubuntu-Deployment.md)
- [Ubuntu 服务器部署指南（中文版）](docs/Ubuntu-Deployment-CN.md)
- [Ubuntu 部署简版 Runbook（中文版）](docs/Ubuntu-Deployment-Runbook-CN.md)
- [Future Features Index](docs/future-features/README.md)
- [User Service README](user-service/README.md)
- [Product Service README](product-service/README.md)
- [Order Service README](order-service/README.md)
- [Payment Service README](payment-service/README.md)
- [Message Service README](message-service/README.md)
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
