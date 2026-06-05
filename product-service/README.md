# Product Service

商品服务，提供商品管理、搜索、分类管理、库存管理等核心功能。

## 功能列表

### 1. 商品管理
- ✅ 创建商品
- ✅ 更新商品信息
- ✅ 删除商品（软删除）
- ✅ 获取商品详情
- ✅ 获取商品列表（分页、按分类）
- ✅ 商品上下架
- ✅ 检查商品状态

### 2. 商品搜索
- ✅ 使用Elasticsearch全文搜索
- ✅ 多字段匹配（商品名称、描述）
- ✅ 分页查询
- ✅ 搜索结果缓存

### 3. 分类管理
- ✅ 获取所有分类（树形结构）
- ✅ 获取分类的子分类
- ✅ 获取分类完整路径
- ✅ 分类商品数量统计

### 4. 库存管理
- ✅ 单个商品库存扣减（分布式锁）
- ✅ 单个商品库存增加
- ✅ 批量扣减库存
- ✅ 检查库存是否充足
- ✅ 库存变化记录

### 5. 数据统计
- ✅ 在售商品数量统计
- ✅ 热门商品（销量前N）
- ✅ 最新商品
- ✅ 分类商品数量分布

## 技术实现

### 依赖技术
- **Spring Boot 3.2.0**: 基础框架
- **Spring Cloud**: 微服务架构
- **MyBatis Plus 3.5.5**: ORM框架
- **Elasticsearch**: 全文搜索
- **Redis**: 缓存
- **Common Domain**: 基础实体类
- **Common Starter**: 通用组件

### 核心功能实现

#### 1. 商品创建
```java
Long productId = productService.createProduct(CreateProductRequest request);
```
- 检查商品名称是否重复
- 设置默认状态为上架
- 初始销量为0
- 记录创建时间

#### 2. 商品搜索（Elasticsearch）
```java
PageResult<ProductDTO> result = productService.searchProducts("iPhone", 1, 10);
```
- 使用MultiMatchQuery进行多字段匹配
- 过滤在售且未删除的商品
- 支持分页查询

#### 3. 库存扣减（分布式锁）
```java
productService.deductStock(productId, quantity);
```
- 使用Redis分布式锁防止并发扣减
- 先检查库存是否充足
- 事务保证扣减的原子性
- 更新Elasticsearch索引

#### 4. 分类管理
```java
List<CategoryDTO> categories = productService.getChildrenCategories(parentId);
```
- 支持多层级分类
- 递归查询子分类
- 获取分类完整路径

## API接口

### 基础路径
`http://localhost:8083/api/product`

### 接口列表

#### 创建商品
```
POST /create
Content-Type: application/json

{
  "categoryId": 1,
  "productName": "iPhone 15 Pro",
  "productDesc": "A17 Pro芯片，钛金属设计",
  "productImage": "https://example.com/iphone.jpg",
  "unitPrice": 7999.00,
  "stock": 100
}
```

#### 获取商品详情
```
GET /{productId}
```

#### 获取商品列表（按分类）
```
GET /category/{categoryId}?current=1&size=10
```

#### 搜索商品
```
GET /search?keyword=iPhone&current=1&size=10
```

#### 获取所有商品
```
GET /list?current=1&size=10
```

#### 上架/下架商品
```
POST /{productId}/online
POST /{productId}/offline
```

#### 检查商品是否在售
```
GET /{productId}/on-sale
```

#### 扣减库存
```
POST /{productId}/stock/deduct?quantity=1
```

#### 检查库存
```
GET /{productId}/stock/check?quantity=1
```

#### 批量扣减库存
```
POST /batch/stock/deduct
Content-Type: application/json

{
  "1": 2,
  "2": 1,
  "3": 1
}
```

#### 获取所有分类
```
GET /categories
```

#### 获取分类的子分类
```
GET /categories/{categoryId}/children
```

#### 获取分类完整路径
```
GET /categories/{categoryId}/path
```

#### 获取在售商品数量
```
GET /count/on-sale
```

#### 获取热门商品
```
GET /top-sales?limit=10
```

#### 获取最新商品
```
GET /latest?limit=10
```

#### 获取商品数量分布
```
GET /count/distribution?limit=20
```

## 架构设计

### 服务架构
```
Product Service
├── Controller (ProductController) - 处理HTTP请求
├── Service (ProductService) - 业务逻辑
├── Mapper (ProductMapper, CategoryMapper) - 数据访问
├── Entity (ProductDO, CategoryDO) - 数据库实体
├── DTO (CreateProductRequest, UpdateProductRequest, etc.) - 数据传输对象
└── Repository (ElasticsearchRepository) - 搜索存储
```

### 数据流程

#### 1. 创建商品
```
Controller → Service (createProduct)
           → Mapper.insert
           → Redis清除缓存
           → Elasticsearch保存
```

#### 2. 搜索商品
```
Controller → Service (searchProducts)
           → Elasticsearch.search
           → 结果转换为DTO
           → 缓存结果
```

#### 3. 扣减库存
```
Controller → Service (deductStock)
           → Redis分布式锁 (executeWithLock)
           → Mapper.selectById (检查库存)
           → Mapper.updateById (扣减库存)
           → Redis清除缓存
           → Elasticsearch更新
```

#### 4. 分类查询
```
Controller → Service (getChildrenCategories)
           → Mapper.selectChildrenCategories
           → 递归查询所有子分类
           → 返回树形结构
```

### 缓存策略
- **商品信息缓存**: Key `product:{productId}`, TTL 1小时
- **分类列表缓存**: Key `categories:*`, TTL 30分钟
- **更新时清除**: 更新商品或分类时同步清除相关缓存

### 搜索策略
- **Elasticsearch索引**: 所有商品数据同步到Elasticsearch
- **多字段匹配**: 商品名称、商品描述
- **过滤条件**: status=1 (在售), deleted=0 (未删除)
- **排序**: 销量降序、创建时间降序

### 分布式锁策略
- **锁类型**: Redis SetNX
- **锁粒度**: `product:stock:{productId}`
- **超时时间**: 30秒
- **重试机制**: 使用executeWithLock自动处理

## 数据库表

### t_product (商品表)
| 字段 | 类型 | 说明 |
|------|------|------|
| product_id | BIGINT | 商品ID（主键，自增） |
| category_id | BIGINT | 分类ID |
| product_name | VARCHAR(200) | 商品名称 |
| product_desc | TEXT | 商品描述 |
| product_image | VARCHAR(255) | 商品图片 |
| unit_price | DECIMAL(10,2) | 单价 |
| stock | INT | 库存 |
| status | TINYINT | 状态（0下架，1上架，2删除） |
| sales_count | INT | 销量 |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

### t_category (分类表)
| 字段 | 类型 | 说明 |
|------|------|------|
| category_id | BIGINT | 分类ID（主键，自增） |
| parent_id | BIGINT | 父分类ID |
| category_name | VARCHAR(50) | 分类名称 |
| level | TINYINT | 层级（1-一级，2-二级，3-三级） |
| sort_order | INT | 排序 |
| status | TINYINT | 状态（0禁用，1启用） |
| create_time | DATETIME | 创建时间 |
| update_time | DATETIME | 更新时间 |

## Elasticsearch Schema

### 商品索引
```json
{
  "product_id": 1,
  "category_id": 1,
  "product_name": "iPhone 15 Pro",
  "product_desc": "A17 Pro芯片，钛金属设计",
  "product_image": "https://example.com/iphone.jpg",
  "unit_price": 7999.00,
  "stock": 100,
  "status": 1,
  "sales_count": 50,
  "create_time": "2024-05-29T10:00:00"
}
```

**索引映射**:
```json
{
  "properties": {
    "product_id": {"type": "long"},
    "category_id": {"type": "long"},
    "product_name": {"type": "text", "analyzer": "ik_max_word"},
    "product_desc": {"type": "text", "analyzer": "ik_max_word"},
    "unit_price": {"type": "double"},
    "stock": {"type": "integer"},
    "status": {"type": "integer"},
    "sales_count": {"type": "integer"},
    "create_time": {"type": "date"}
  }
}
```

## 使用示例

### 创建和搜索商品
```bash
# 创建商品
curl -X POST http://localhost:8083/api/product/create \
  -H "Content-Type: application/json" \
  -d '{
    "categoryId": 2,
    "productName": "iPhone 15 Pro",
    "productDesc": "A17 Pro芯片，钛金属设计",
    "productImage": "https://example.com/iphone.jpg",
    "unitPrice": 7999.00,
    "stock": 100
  }'

# 搜索商品
curl -X GET "http://localhost:8083/api/product/search?keyword=iPhone&current=1&size=10"
```

### 分类操作
```bash
# 获取所有分类
curl -X GET http://localhost:8083/api/product/categories

# 获取子分类
curl -X GET "http://localhost:8083/api/product/categories/1/children"

# 获取分类路径
curl -X GET "http://localhost:8083/api/product/categories/3/path"
```

### 库存管理
```bash
# 扣减库存
curl -X POST "http://localhost:8083/api/product/1/stock/deduct?quantity=1"

# 检查库存
curl -X GET "http://localhost:8083/api/product/1/stock/check?quantity=1"

# 批量扣减
curl -X POST http://localhost:8083/api/product/batch/stock/deduct \
  -H "Content-Type: application/json" \
  -d '{"1": 2, "2": 1, "3": 1}'
```

### 统计功能
```bash
# 获取在售商品数量
curl -X GET http://localhost:8083/api/product/count/on-sale

# 获取热门商品
curl -X GET "http://localhost:8083/api/product/top-sales?limit=10"

# 获取最新商品
curl -X GET "http://localhost:8083/api/product/latest?limit=10"

# 获取商品数量分布
curl -X GET "http://localhost:8083/api/product/count/distribution?limit=20"
```

## 注意事项

1. **商品名称唯一性**: 同一分类下商品名称不能重复
2. **库存扣减并发**: 使用分布式锁保证库存扣减的原子性
3. **缓存一致性**: 更新商品或分类时同步清除缓存
4. **Elasticsearch索引**: 商品增删改查时同步更新Elasticsearch
5. **软删除**: 商品删除时设置deleted=1，不真正删除数据
6. **分页查询**: 使用MyBatis Plus和Elasticsearch分页插件

## 扩展功能

- [ ] 图片上传（OSS）
- [ ] 商品图片轮播
- [ ] 商品评价系统
- [ ] 商品收藏
- [ ] 商品比较
- [ ] 商品推荐算法
- [ ] 搜索建议
- [ ] 商品详情页

## 相关技术文档

- [MyBatis Plus文档](https://baomidou.com/)
- [Elasticsearch文档](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Redis文档](https://redis.io/documentation)
- [Spring Cloud文档](https://spring.io/projects/spring-cloud)
