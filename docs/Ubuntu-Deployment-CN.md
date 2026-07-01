# Ubuntu 服务器部署指南

本文档说明如何在一台新装的 Ubuntu Server 上部署 Spring Cloud Learning System。推荐采用“中间件 Docker Compose + Java 服务宿主机运行”的混合方案：基础设施用容器统一拉起，业务服务使用 Maven / systemd 方式运行，便于调试和复盘。

项目总览见：`README.md`

## 1. 环境准备

### 推荐系统
- Ubuntu 22.04 LTS
- Ubuntu 24.04 LTS 也可使用，但本文以 22.04 为参考环境

### 最低资源建议
- CPU：2 核
- 内存：8 GB 起步，建议 16 GB
- 磁盘：至少 40 GB 可用空间

### 需要开放的端口
| 端口 | 服务 |
|------|------|
| 3306 | MySQL |
| 6379 | Redis |
| 8848 | Nacos |
| 9200 | Elasticsearch HTTP |
| 9300 | Elasticsearch Transport |
| 27017 | MongoDB |
| 8080 | xxl-job Admin |
| 8082 | User Service |
| 8083 | Product Service |
| 8084 | Order Service |
| 8085 | Payment Service |
| 8086 | Message Service |
| 8087 | Scheduler Service |
| 8088 | Admin Service |

### 检查系统信息
```bash
lsb_release -a
lscpu
free -h
df -h
```

预期结果：
- 系统版本为 Ubuntu 22.04 或更高
- CPU / 内存 / 磁盘满足基础运行要求

### 放通防火墙端口
```bash
sudo ufw status
sudo ufw allow 3306/tcp
sudo ufw allow 6379/tcp
sudo ufw allow 8848/tcp
sudo ufw allow 9200/tcp
sudo ufw allow 9300/tcp
sudo ufw allow 27017/tcp
sudo ufw allow 8080/tcp
sudo ufw allow 8082:8088/tcp
sudo ufw enable
sudo ufw status verbose
```

预期结果：
- 对应端口被放行
- `ufw status verbose` 能看到生效规则

## 2. 安装基础工具

### 更新系统
```bash
sudo apt update
sudo apt -y upgrade
```

### 安装常用工具
```bash
sudo apt install -y git curl vim unzip ca-certificates gnupg lsb-release
```

验证：
```bash
git --version
curl --version
vim --version | head -n 1
```

### 安装 Docker
WSL2 环境推荐优先使用 Docker Desktop：

1. Windows 安装 Docker Desktop
2. Docker Desktop 设置中启用 `Use the WSL 2 based engine`
3. 在 `Resources -> WSL integration` 中启用当前 Ubuntu 发行版
4. 回到 Ubuntu 执行验证命令

如果希望完全在 Ubuntu 内安装 Docker Engine，请使用 Docker 官方 apt 源。Ubuntu 24.04 默认源里的 `docker.io` 不一定提供 `docker-compose-plugin`，直接安装会出现 `E: Unable to locate package docker-compose-plugin`。

```bash
sudo apt update
sudo apt install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER
newgrp docker
```

如果 WSL2 中 `systemctl` 不可用，先在 Windows PowerShell 中执行：

```powershell
wsl --shutdown
```

然后在 Ubuntu 中启用 systemd：

```bash
sudo tee /etc/wsl.conf <<'EOF'
[boot]
systemd=true
EOF
```

再重启 Ubuntu 终端后继续执行 Docker 启动命令。

验证：
```bash
docker --version
docker compose version
docker ps
```

预期结果：
- Docker 服务已启动
- 当前用户可以直接执行 `docker ps`

### 安装 OpenJDK 17
```bash
sudo apt install -y openjdk-17-jdk
```

验证：
```bash
java -version
javac -version
```

预期结果：
- Java 版本为 17

### 安装 Maven
```bash
sudo apt install -y maven
```

验证：
```bash
mvn -version
```

预期结果：
- Maven 3.8+ 可用

## 3. 获取项目代码

### 克隆仓库
```bash
git clone <你的仓库地址> spring-cloud-learning-system
cd spring-cloud-learning-system
```

### 检查目录
```bash
ls -la
ls common
ls scripts
```

预期结果：
- 根目录存在 `pom.xml`
- `common/`、`scripts/` 和各服务目录都存在

## 4. 启动基础设施

本文假设 `docker/docker-compose.yml` 已包含以下中间件：MySQL、Redis、MongoDB、Elasticsearch、Nacos、xxl-job admin。

### 启动 MySQL
```bash
docker compose up -d mysql
```

验证：
```bash
docker ps --filter name=mysql
```

预期结果：
- 容器状态为 `Up`

等待 15~30 秒后再继续下一步。

### 启动 Redis
```bash
docker compose up -d redis
```

验证：
```bash
docker ps --filter name=redis
```

预期结果：
- 容器状态为 `Up`

### 启动 MongoDB
```bash
docker compose up -d mongo
```

验证：
```bash
docker ps --filter name=mongo
```

预期结果：
- 容器状态为 `Up`

### 启动 Elasticsearch
如果系统内核参数不够，先执行：

```bash
sudo sysctl -w vm.max_map_count=262144
```

然后启动：

```bash
docker compose up -d elasticsearch
```

验证：
```bash
curl -s http://localhost:9200
```

预期结果：
- 返回 Elasticsearch 的 JSON 信息

### 启动 Nacos
```bash
docker compose up -d nacos
```

验证：
```bash
curl -I http://localhost:8848/nacos
```

预期结果：
- 返回 200 或 302，说明服务已可用

### 启动 xxl-job admin
```bash
docker compose up -d xxl-job-admin
```

验证：
```bash
curl -I http://localhost:8080/xxl-job-admin
```

预期结果：
- 能访问 xxl-job 管理端

## 5. 初始化数据库

### 等待 MySQL 就绪
```bash
until docker exec mysql mysqladmin ping -h 127.0.0.1 -uroot -proot --silent; do sleep 2; done
```

预期结果：
- MySQL 准备好后命令才会退出

### 初始化主数据库
```bash
docker exec -i mysql mysql -uroot -proot < scripts/mysql/init.sql
```

### 初始化 xxl-job 数据库
```bash
docker exec -i mysql mysql -uroot -proot < scripts/mysql/xxl-job.sql
```

### 初始化 seata 数据库结构
```bash
docker exec -i mysql mysql -uroot -proot < scripts/mysql/seata.sql
```

### 验证数据库
```bash
docker exec -it mysql mysql -uroot -proot -e "SHOW DATABASES;"
docker exec -it mysql mysql -uroot -proot -e "USE learning_system; SHOW TABLES;"
docker exec -it mysql mysql -uroot -proot -e "USE xxl_job; SHOW TABLES;"
docker exec -it mysql mysql -uroot -proot -e "USE seata; SHOW TABLES;"
```

预期结果：
- 存在 `learning_system`、`xxl_job`、`seata`
- 对应表结构已创建完成

## 6. 构建并启动服务

### 先构建 common 模块
```bash
mvn clean install -pl common/common-domain,common/common-util,common/common-api,common/common-starter -DskipTests
```

预期结果：
- 构建成功
- 根目录父 POM 能正常解析

如果 Maven 报父 POM 解析失败，请确认在仓库根目录执行命令，并先清理本地缓存中陈旧的 `com.learning:learning-system-parent` 条目后再试。

### 启动 User Service
```bash
cd user-service
mvn spring-boot:run
```

预期结果：
- 控制台出现 `Started UserServiceApplication`
- 监听 `8082`

### 启动 Product Service
```bash
cd ../product-service
mvn spring-boot:run
```

预期结果：
- 控制台出现 `Started ProductServiceApplication`
- 监听 `8083`

### 启动 Order Service
```bash
cd ../order-service
mvn spring-boot:run
```

预期结果：
- 控制台出现 `Started OrderServiceApplication`
- 监听 `8084`

### 启动 Payment Service
```bash
cd ../payment-service
mvn spring-boot:run
```

预期结果：
- 控制台出现 `Started PaymentServiceApplication`
- 监听 `8085`

### 启动 Message Service
```bash
cd ../message-service
mvn spring-boot:run
```

预期结果：
- 控制台出现 `Started MessageServiceApplication`
- 监听 `8086`

### 启动 Scheduler Service
```bash
cd ../scheduler-service
mvn spring-boot:run
```

预期结果：
- 控制台出现 `Started SchedulerServiceApplication`
- 监听 `8087`

### 启动 Admin Service
```bash
cd ../admin-service
mvn spring-boot:run
```

预期结果：
- 控制台出现 `Started AdminServiceApplication`
- 监听 `8088`

### 可选：使用 systemd 常驻运行
下面是一个可复用的模板，把 `Description`、`WorkingDirectory`、服务目录名替换成对应服务即可：

```ini
[Unit]
Description=User Service
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/spring-cloud-learning-system/user-service
ExecStart=/usr/bin/mvn spring-boot:run
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

启用并启动：
```bash
sudo systemctl daemon-reload
sudo systemctl enable user-service
sudo systemctl start user-service
sudo systemctl status user-service
```

其他服务同样处理，只需要替换服务名与工作目录。

## 7. 验证部署成功

### User Service
```bash
curl -s -X POST http://localhost:8082/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123","phone":"13800138000","email":"test@example.com"}'

curl -s -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

### Product Service
```bash
curl -s -X POST http://localhost:8083/api/product/create \
  -H "Content-Type: application/json" \
  -d '{"categoryId":1,"productName":"Demo","unitPrice":99.00,"stock":100}'
```

### Order Service
```bash
curl -s -X POST http://localhost:8084/api/order/create \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{"productId":1,"productName":"Demo","quantity":1,"unitPrice":99.00,"totalPrice":99.00,"paymentMethod":3,"address":"Shenzhen","receiver":"Zhang","receiverPhone":"13800138000"}'
```

### Payment Service
```bash
curl -s -X POST http://localhost:8085/api/payment/create \
  -H "Content-Type: application/json" \
  -d '{"orderId":1,"userId":1,"paymentMethod":3,"amount":99.00}'
```

### Message Service
```bash
curl -s -X POST http://localhost:8086/api/message/create \
  -H "Content-Type: application/json" \
  -d '{"userId":1,"messageType":1,"title":"Test","content":"test","important":0}'
```

### Admin Service
```bash
curl -s -X POST http://localhost:8088/api/admin/logs/operation \
  -H "Content-Type: application/json" \
  -d '{"serviceName":"test","operationType":"TEST","businessType":"test","resultStatus":200,"resultMessage":"ok"}'

curl -s http://localhost:8088/api/admin/logs/statistics/overview
```

### Nacos 检查
打开：
```text
http://<server-ip>:8848/nacos
```

预期结果：
- 已启动的服务能在实例列表中看到

## 8. 常见问题排查

| 问题 | 可能原因 | 解决办法 |
|------|---------|---------|
| `docker: command not found` | Docker 未安装 | 安装 Docker 和 Docker Compose 插件 |
| `E: Unable to locate package docker-compose-plugin` | 使用了 Ubuntu 默认源，未配置 Docker 官方 apt 源 | 按“安装 Docker”章节添加 Docker 官方源，或在 Windows 安装 Docker Desktop 并启用 WSL integration |
| `Cannot connect to the Docker daemon` | 当前用户未加入 docker 组 | 执行 `sudo usermod -aG docker $USER` 后重新登录 |
| Java 找不到 | JDK 未安装 | 安装 `openjdk-17-jdk` |
| Maven 父 POM 解析失败 | 没在仓库根目录执行或本地缓存过旧 | 回到仓库根目录执行，刷新 Maven 缓存后重试 |
| MySQL 连接被拒绝 | 容器还没完全启动 | 等待并重新检查容器状态 |
| Nacos 打不开 | 服务启动较慢 | 等 30~60 秒，查看容器日志 |
| Elasticsearch 退出码 78 | `vm.max_map_count` 太小 | 执行 `sudo sysctl -w vm.max_map_count=262144` |
| 端口被占用 | 旧进程还在 | 用 `ss -lntp` 查找并停止进程 |
| MongoDB 连不上 | 容器未启动或端口未放通 | 检查 `docker ps` 和防火墙规则 |

### 停止与清理
```bash
pkill -f spring-boot:run
docker compose down
# 可选：删除数据卷
docker compose down -v
```

## 9. 后续扩展

- 以后可以把 Seata Server 也纳入 Docker Compose
- Gateway Service 可以在路由规则完善后加入部署流程
- Chat Server 可在 Netty 实现后单独部署
- 未来也可以把 Java 服务全部容器化
