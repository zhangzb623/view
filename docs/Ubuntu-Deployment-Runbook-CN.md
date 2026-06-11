# Ubuntu 部署简版 Runbook

适用场景：把 Spring Cloud Learning System 部署到一台新装 Ubuntu 22.04 Server 上。

推荐方案：**中间件 Docker Compose + Java 服务宿主机运行**。

## 0. 先决条件
- Ubuntu 22.04
- 8GB 内存起步，16GB 更稳
- 已开放端口：3306、6379、8848、9200、9300、27017、8080、8082~8088

## 1. 安装工具
```bash
sudo apt update
sudo apt install -y git curl vim unzip ca-certificates gnupg lsb-release docker.io docker-compose-plugin openjdk-17-jdk maven
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER
newgrp docker
```

验证：
```bash
docker --version
docker compose version
java -version
mvn -version
```

## 2. 拉代码
```bash
git clone <你的仓库地址> spring-cloud-learning-system
cd spring-cloud-learning-system
```

## 3. 启动基础设施
### 3.1 MySQL
```bash
docker compose up -d mysql
```

### 3.2 Redis
```bash
docker compose up -d redis
```

### 3.3 MongoDB
```bash
docker compose up -d mongo
```

### 3.4 Elasticsearch
```bash
sudo sysctl -w vm.max_map_count=262144
docker compose up -d elasticsearch
```

### 3.5 Nacos
```bash
docker compose up -d nacos
```

### 3.6 xxl-job admin
```bash
docker compose up -d xxl-job-admin
```

检查：
```bash
docker ps
curl -s http://localhost:9200
curl -I http://localhost:8848/nacos
curl -I http://localhost:8080/xxl-job-admin
```

## 4. 初始化数据库
```bash
until docker exec mysql mysqladmin ping -h 127.0.0.1 -uroot -proot --silent; do sleep 2; done
docker exec -i mysql mysql -uroot -proot < scripts/mysql/init.sql
docker exec -i mysql mysql -uroot -proot < scripts/mysql/xxl-job.sql
docker exec -i mysql mysql -uroot -proot < scripts/mysql/seata.sql
```

检查：
```bash
docker exec -it mysql mysql -uroot -proot -e "SHOW DATABASES;"
```

## 5. 构建 common 模块
```bash
mvn clean install -pl common/common-domain,common/common-util,common/common-api,common/common-starter -DskipTests
```

## 6. 启动服务
按顺序启动：

```bash
cd user-service && mvn spring-boot:run
cd ../product-service && mvn spring-boot:run
cd ../order-service && mvn spring-boot:run
cd ../payment-service && mvn spring-boot:run
cd ../message-service && mvn spring-boot:run
cd ../scheduler-service && mvn spring-boot:run
cd ../admin-service && mvn spring-boot:run
```

端口：
- User 8082
- Product 8083
- Order 8084
- Payment 8085
- Message 8086
- Scheduler 8087
- Admin 8088

## 7. 快速验证
### User
```bash
curl -s -X POST http://localhost:8082/api/user/register -H "Content-Type: application/json" -d '{"username":"testuser","password":"password123","phone":"13800138000","email":"test@example.com"}'
```

### Product
```bash
curl -s -X POST http://localhost:8083/api/product/create -H "Content-Type: application/json" -d '{"categoryId":1,"productName":"Demo","unitPrice":99.00,"stock":100}'
```

### Order
```bash
curl -s -X POST http://localhost:8084/api/order/create -H "Content-Type: application/json" -H "X-User-Id: 1" -d '{"productId":1,"productName":"Demo","quantity":1,"unitPrice":99.00,"totalPrice":99.00,"paymentMethod":3,"address":"Shenzhen","receiver":"Zhang","receiverPhone":"13800138000"}'
```

### Payment
```bash
curl -s -X POST http://localhost:8085/api/payment/create -H "Content-Type: application/json" -d '{"orderId":1,"userId":1,"paymentMethod":3,"amount":99.00}'
```

### Message
```bash
curl -s -X POST http://localhost:8086/api/message/create -H "Content-Type: application/json" -d '{"userId":1,"messageType":1,"title":"Test","content":"test","important":0}'
```

### Admin
```bash
curl -s -X POST http://localhost:8088/api/admin/logs/operation -H "Content-Type: application/json" -d '{"serviceName":"test","operationType":"TEST","businessType":"test","resultStatus":200,"resultMessage":"ok"}'
curl -s http://localhost:8088/api/admin/logs/statistics/overview
```

## 8. 常见故障
- Maven 父 POM 报错：回到仓库根目录重试
- Docker 起不来：检查 `docker ps` 和 `systemctl status docker`
- Elasticsearch 起不来：先执行 `vm.max_map_count=262144`
- Nacos 打不开：等 30~60 秒再试

## 9. 停止
```bash
pkill -f spring-boot:run
docker compose down
```
