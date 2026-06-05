# User Service

用户服务，提供用户管理相关功能。

## 功能列表

### 1. 用户管理
- ✅ 用户注册
- ✅ 用户登录（获取Token）
- ✅ 获取用户信息
- ✅ 更新用户信息
- ✅ 获取用户列表（分页）
- ✅ 删除用户（软删除）

### 2. 地址管理
- ✅ 添加收货地址
- ✅ 获取地址列表
- ✅ 更新地址
- ✅ 删除地址
- ✅ 设置默认地址

### 3. 余额管理
- ✅ 扣减余额（分布式锁保证）
- ✅ 增加余额
- ✅ 查询余额（缓存）

## 技术实现

### 依赖技术
- **Spring Boot 3.2.0**: 基础框架
- **Spring Cloud**: 微服务架构
- **MyBatis Plus 3.5.5**: ORM框架
- **Spring Security**: 安全认证
- **JWT**: Token生成和验证
- **Redis**: 缓存
- **Common Domain**: 基础实体类
- **Common Starter**: 通用组件

### 核心功能实现

#### 1. 用户注册
```java
Long userId = userService.register(CreateUserRequest request);
```
- 检查用户名、手机号、邮箱是否已存在
- 密码使用BCrypt加密存储
- 初始余额为0

#### 2. 用户登录
```java
LoginResponse response = userService.login(LoginRequest request);
// 返回token, userId, username, email
```
- 验证用户名和密码
- 生成JWT Token
- 缓存用户信息到Redis（1.5小时TTL）

#### 3. 分布式锁扣减余额
```java
userService.deductBalance(userId, amount);
```
- 使用Redis分布式锁防止并发扣款
- 先检查余额是否充足
- 事务保证余额扣减的原子性

#### 4. Redis缓存
- 用户信息缓存（Key: `user:{userId}`，TTL: 1.5小时）
- 读取时优先从缓存获取
- 更新时同步清除缓存

## API接口

### 基础路径
`http://localhost:8082/api/user`

### 接口列表

#### 注册
```
POST /register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123",
  "phone": "13800138000",
  "email": "test@example.com"
}
```

#### 登录
```
POST /login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}

Response:
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "Bearer xxxxx",
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

#### 获取用户信息
```
GET /{userId}
Authorization: Bearer {token}
```

#### 更新用户信息
```
PUT /{userId}
Authorization: Bearer {token}
Content-Type: application/json

{
  "email": "new@example.com",
  "avatar": "https://example.com/avatar.jpg",
  "phone": "13800138001"
}
```

#### 获取用户列表（分页）
```
GET /list?current=1&size=10
Authorization: Bearer {token}
```

#### 删除用户
```
DELETE /{userId}
Authorization: Bearer {token}
```

#### 添加地址
```
POST /address/add?userId={userId}
Content-Type: application/json

{
  "receiverName": "张三",
  "receiverPhone": "13800138000",
  "province": "广东省",
  "city": "深圳市",
  "district": "南山区",
  "detailAddress": "科技园南区XX路XX号"
}
```

#### 获取地址列表
```
GET /address/list?userId={userId}&current=1&size=10
Authorization: Bearer {token}
```

#### 更新地址
```
PUT /address/{addressId}
Content-Type: application/json

{
  "receiverName": "李四",
  "receiverPhone": "13800138001"
}
```

#### 删除地址
```
DELETE /address/{addressId}
Authorization: Bearer {token}
```

#### 设置默认地址
```
POST /address/default/{addressId}?userId={userId}
Authorization: Bearer {token}
```

#### 扣减余额
```
POST /balance/deduct?userId={userId}&amount=100.00
Authorization: Bearer {token}
```

#### 查询余额
```
GET /balance?userId={userId}
Authorization: Bearer {token}
```

## 架构设计

### 服务架构
```
User Service
├── Controller (UserController) - 处理HTTP请求
├── Service (UserService) - 业务逻辑
├── Mapper (UserMapper, UserAddressMapper) - 数据访问
├── Entity (UserDO, UserAddressDO) - 数据库实体
└── DTO (CreateUserRequest, LoginResponse, etc.) - 数据传输对象
```

### 数据流程
```
1. 请求 → Controller
2. Controller → Service (业务逻辑)
3. Service → Mapper (数据访问)
4. Mapper → MySQL数据库
5. 缓存处理 (Redis)
6. 返回响应
```

### 缓存策略
- **Cache-Aside**: 读取时先读缓存，缓存未命中则读数据库
- **Write-Through**: 更新时先更新数据库，再更新缓存
- **TTL**: 1.5小时

### 分布式锁策略
- **锁类型**: Redis SetNX
- **锁粒度**: `user:balance:{userId}`
- **超时时间**: 30秒
- **重试机制**: 不重试（使用executeWithLock自动处理）

## 数据库表

### t_user_info (用户表)
| 字段 | 类型 | 说明 |
|------|------|------|
| user_id | BIGINT | 用户ID（主键，自增） |
| username | VARCHAR(50) | 用户名（唯一） |
| password | VARCHAR(100) | 密码（BCrypt加密） |
| phone | VARCHAR(20) | 手机号 |
| email | VARCHAR(100) | 邮箱 |
| avatar | VARCHAR(255) | 头像URL |
| balance | DECIMAL(10,2) | 余额 |
| status | TINYINT | 状态（0禁用，1启用） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### t_user_address (地址表)
| 字段 | 类型 | 说明 |
|------|------|------|
| address_id | BIGINT | 地址ID（主键，自增） |
| user_id | BIGINT | 用户ID |
| receiver_name | VARCHAR(50) | 收货人 |
| receiver_phone | VARCHAR(20) | 收货人电话 |
| province | VARCHAR(50) | 省份 |
| city | VARCHAR(50) | 城市 |
| district | VARCHAR(50) | 区县 |
| detail_address | VARCHAR(255) | 详细地址 |
| is_default | TINYINT | 是否默认（0否，1是） |
| status | TINYINT | 状态（0删除，1启用） |

## 使用示例

### 注册和登录
```bash
# 注册
curl -X POST http://localhost:8082/api/user/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "phone": "13800138000",
    "email": "test@example.com"
  }'

# 登录
curl -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 响应
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "Bearer eyJhbGc...",
    "userId": 1,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

### 获取用户信息
```bash
# 替换{TOKEN}为实际的token
curl -X GET http://localhost:8082/api/user/1 \
  -H "Authorization: Bearer {TOKEN}"
```

### 添加地址
```bash
curl -X POST "http://localhost:8082/api/user/address/add?userId=1" \
  -H "Authorization: Bearer {TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "receiverName": "张三",
    "receiverPhone": "13800138000",
    "province": "广东省",
    "city": "深圳市",
    "district": "南山区",
    "detailAddress": "科技园南区XX路XX号"
  }'
```

### 扣减余额
```bash
curl -X POST "http://localhost:8082/api/user/balance/deduct?userId=1&amount=100.00" \
  -H "Authorization: Bearer {TOKEN}"
```

## 注意事项

1. **密码安全**: 使用BCrypt加密存储，永不返回密码
2. **缓存一致性**: 更新数据时同步清除缓存
3. **分布式锁**: 扣减余额时使用分布式锁防止并发问题
4. **软删除**: 用户删除时设置deleted=1，不真正删除数据
5. **分页查询**: 使用MyBatis Plus的分页插件
6. **Swagger文档**: 访问 http://localhost:8082/swagger-ui.html

## 扩展功能

- [ ] 邮箱/短信验证
- [ ] 用户权限管理
- [ ] 用户头像上传（OSS）
- [ ] 支付密码设置
- [ ] 账号冻结/解冻
- [ ] 第三方登录（微信、支付宝）
- [ ] 密码修改
- [ ] 找回密码
