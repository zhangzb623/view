# Windows WSL2 Ubuntu 24.04 部署指南

本文档专门面向以下环境：

- Windows 10/11
- WSL2
- Ubuntu 24.04 LTS
- 在 WSL2 中运行本项目的 Java 服务
- 使用 Docker 运行 MySQL、Redis、MongoDB、Elasticsearch、Nacos、xxl-job 等中间件

如果你是在真正的 Ubuntu Server 上部署，请优先看 `docs/Ubuntu-Deployment-CN.md`。如果是在 Windows 电脑上通过 WSL2 学习和调试，本文件更合适。

## 1. 推荐架构

推荐使用：

```text
Windows
├── Docker Desktop
│   └── Docker Engine / Docker Compose
└── WSL2 Ubuntu 24.04
    ├── Java 17
    ├── Maven
    ├── Git
    └── Spring Cloud Learning System
```

也就是说：

- Docker 不建议优先装在 Ubuntu 里面
- Docker 推荐装在 Windows 的 Docker Desktop
- Ubuntu 通过 WSL integration 使用 Docker Desktop 提供的 `docker` 命令
- Java 服务在 Ubuntu 里用 Maven 启动

这样可以避开 WSL2 里 Docker daemon、systemd、iptables、开机自启等问题。

## 2. Windows 侧准备

### 检查 WSL2

在 Windows PowerShell 中执行：

```powershell
wsl --status
wsl --list --verbose
```

预期结果：

- 默认版本是 WSL2
- Ubuntu 的 `VERSION` 是 `2`

如果 Ubuntu 不是 WSL2：

```powershell
wsl --set-version Ubuntu-24.04 2
```

如果你的发行版名称不是 `Ubuntu-24.04`，以 `wsl --list --verbose` 显示的名称为准。

### 安装 Docker Desktop

1. 安装 Docker Desktop for Windows
2. 打开 Docker Desktop
3. 进入 `Settings -> General`
4. 勾选 `Use the WSL 2 based engine`
5. 进入 `Settings -> Resources -> WSL integration`
6. 启用你的 Ubuntu 发行版
7. 点击 `Apply & restart`

完成后进入 Ubuntu 终端验证：

```bash
docker --version
docker compose version
docker ps
```

如果 `docker ps` 能正常执行，说明 WSL2 已经能使用 Docker Desktop。

## 3. 不推荐但可用：在 Ubuntu 里安装 Docker Engine

如果你不想用 Docker Desktop，也可以把 Docker Engine 安装到 WSL2 Ubuntu 内部。但不要使用下面这条命令：

```bash
sudo apt install -y docker.io docker-compose-plugin
```

原因是 Ubuntu 默认源里的 `docker.io` 不一定提供 `docker-compose-plugin`。在 Ubuntu 24.04 中容易出现：

```text
E: Unable to locate package docker-compose-plugin
```

正确做法是使用 Docker 官方 apt 源：

```bash
sudo apt update
sudo apt install -y ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

sudo tee /etc/apt/sources.list.d/docker.sources <<EOF
Types: deb
URIs: https://download.docker.com/linux/ubuntu
Suites: $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}")
Components: stable
Architectures: $(dpkg --print-architecture)
Signed-By: /etc/apt/keyrings/docker.asc
EOF

sudo apt update
sudo apt install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
```

如果 `systemctl` 不可用，先启用 WSL systemd：

```bash
sudo tee /etc/wsl.conf <<'EOF'
[boot]
systemd=true
EOF
```

然后在 Windows PowerShell 中重启 WSL：

```powershell
wsl --shutdown
```

重新打开 Ubuntu 后执行：

```bash
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER
newgrp docker
docker ps
```

## 4. Ubuntu 基础工具

进入 Ubuntu 后执行：

```bash
sudo apt update
sudo apt install -y git curl vim unzip ca-certificates gnupg lsb-release openjdk-17-jdk maven
```

验证：

```bash
java -version
javac -version
mvn -version
git --version
```

预期：

- Java 版本是 17
- Maven 可以正常执行

## 5. 项目目录建议

WSL2 可以直接访问 Windows 磁盘：

```bash
cd /mnt/d/coding_file/view
```

但长期开发更推荐把项目放在 WSL2 Linux 文件系统里，例如：

```bash
mkdir -p ~/projects
cd ~/projects
git clone <你的仓库地址> spring-cloud-learning-system
cd spring-cloud-learning-system
```

原因：

- `/mnt/c`、`/mnt/d` 是 Windows 文件系统挂载，Maven 构建和大量文件扫描会慢一些
- `~/projects` 在 WSL2 Linux 文件系统内，性能通常更好

如果你已经在 `D:\coding_file\view` 开发，也可以继续使用：

```bash
cd /mnt/d/coding_file/view
```

## 6. 中间件 Compose 文件

当前仓库没有内置 `docker-compose.yml`。如果后续没有补充 compose 文件，可以先在项目根目录创建：

```bash
mkdir -p docker
vim docker/docker-compose.middleware.yml
```

写入下面内容作为本地开发版中间件：

```yaml
services:
  mysql:
    image: mysql:8.0
    container_name: learning-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root
      TZ: Asia/Shanghai
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    command:
      - --character-set-server=utf8mb4
      - --collation-server=utf8mb4_unicode_ci

  redis:
    image: redis:7.2
    container_name: learning-redis
    ports:
      - "6379:6379"
    command: redis-server --requirepass root
    volumes:
      - redis_data:/data

  mongo:
    image: mongo:7.0
    container_name: learning-mongo
    ports:
      - "27017:27017"
    volumes:
      - mongo_data:/data/db

  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:7.17.22
    container_name: learning-elasticsearch
    environment:
      discovery.type: single-node
      ES_JAVA_OPTS: -Xms512m -Xmx512m
      xpack.security.enabled: "false"
    ports:
      - "9200:9200"
      - "9300:9300"
    volumes:
      - es_data:/usr/share/elasticsearch/data

  nacos:
    image: nacos/nacos-server:v2.3.2
    container_name: learning-nacos
    environment:
      MODE: standalone
      SPRING_DATASOURCE_PLATFORM: ""
      NACOS_AUTH_ENABLE: "false"
    ports:
      - "8848:8848"
      - "9848:9848"

  xxl-job-admin:
    image: xuxueli/xxl-job-admin:2.4.0
    container_name: learning-xxl-job-admin
    environment:
      PARAMS: >-
        --spring.datasource.url=jdbc:mysql://mysql:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai
        --spring.datasource.username=root
        --spring.datasource.password=root
    ports:
      - "8080:8080"
    depends_on:
      - mysql

volumes:
  mysql_data:
  redis_data:
  mongo_data:
  es_data:
```

启动中间件：

```bash
docker compose -f docker/docker-compose.middleware.yml up -d
```

查看状态：

```bash
docker compose -f docker/docker-compose.middleware.yml ps
```

## 7. WSL2 Elasticsearch 参数

Elasticsearch 通常需要调高 `vm.max_map_count`：

```bash
sudo sysctl -w vm.max_map_count=262144
```

验证：

```bash
sysctl vm.max_map_count
```

如果每次重启 WSL 后丢失，可以临时重新执行。学习环境不需要先做永久配置。

## 8. 初始化数据库

等待 MySQL 容器启动：

```bash
until docker exec learning-mysql mysqladmin ping -h 127.0.0.1 -uroot -proot --silent; do sleep 2; done
```

执行初始化脚本：

```bash
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/init.sql
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/xxl-job.sql
docker exec -i learning-mysql mysql -uroot -proot < scripts/mysql/seata.sql
```

如果你之前执行 `init.sql` 时遇到类似下面的错误：

```text
ERROR 1146 (42S02): Table 'distribution_db.t_user_info' doesn't exist
```

说明旧版本 `init.sql` 在新增 `distribution_db` 后，没有在测试数据插入前切回 `user_db` / `product_db` / `payment_db`。当前仓库已修复这个问题，重新执行最新的 `scripts/mysql/init.sql` 即可。

如果你之前执行 `seata.sql` 时遇到字段不匹配或索引引用不存在的问题，说明旧版本脚本里有不合理的 Seata 表结构（例如索引引用了未定义的 `status` 字段，或者混入了与当前 Seata 初始化不一致的字段命名）。当前仓库已整理成更安全的初始化版本，建议直接执行最新版 `scripts/mysql/seata.sql`。

如果你重跑 `scripts/mysql/xxl-job.sql` 时看到类似下面的错误：

```text
ERROR 1050 (42S01): Table 'xxl_job_info' already exists
```

说明旧版本脚本不是幂等的。当前仓库已经把 `xxl-job.sql` 整理成可重复执行版本：
- 建表使用 `CREATE TABLE IF NOT EXISTS`
- 默认管理员使用 `INSERT IGNORE`

如果你已经拉到最新代码，重新执行最新 `scripts/mysql/xxl-job.sql` 即可。

如果你登录 `xxl-job-admin` 后看到类似下面的错误：

```text
Table 'xxl_job.xxl_job_log_report' doesn't exist
```

说明旧版本 `scripts/mysql/xxl-job.sql` 缺少 `xxl_job_log_report`（以及可能缺少 `xxl_job_lock`）。当前仓库已补齐，建议重新执行最新版 `scripts/mysql/xxl-job.sql`。

验证数据库：

```bash
docker exec -it learning-mysql mysql -uroot -proot -e "SHOW DATABASES;"
```

至少应该能看到：

- `learning_system`
- `user_db`
- `product_db`
- `xxl_job`
- `seata`

## 9. 构建项目

在项目根目录执行：

```bash
mvn clean install -DskipTests
```

如果只想先构建公共模块：

```bash
mvn clean install -pl common/common-domain,common/common-util,common/common-api,common/common-starter -DskipTests
```

如果 Maven 下载依赖很慢，可以配置国内镜像，但建议先确认网络能正常访问 Maven Central。

## 10. 启动服务顺序

建议先启动最基础的服务：

```bash
cd user-service
mvn spring-boot:run
```

新开一个 Ubuntu 终端：

```bash
cd /mnt/d/coding_file/view/product-service
mvn spring-boot:run
```

再新开一个终端：

```bash
cd /mnt/d/coding_file/view/payment-service
mvn spring-boot:run
```

如果你的项目放在 WSL2 Linux 文件系统里，把路径替换成实际路径，例如：

```bash
cd ~/projects/spring-cloud-learning-system/user-service
```

服务端口：

| 服务 | 端口 |
|------|------|
| xxl-job Admin | 8080 |
| User Service | 8082 |
| Product Service | 8083 |
| Order Service | 8084 |
| Payment Service | 8085 |
| Message Service | 8086 |
| Scheduler Service | 8087 |
| Admin Service | 8088 |
| Nacos | 8848 |
| Elasticsearch | 9200 |

Windows 浏览器可以直接访问：

```text
http://localhost:8848/nacos
http://localhost:9200
http://localhost:8082
```

## 11. 验证接口

用户注册：

```bash
curl -s -X POST http://localhost:8082/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123","phone":"13800138000","email":"test@example.com"}'
```

用户登录：

```bash
curl -s -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

创建商品：

```bash
curl -s -X POST http://localhost:8083/api/product/create \
  -H "Content-Type: application/json" \
  -d '{"categoryId":1,"productName":"Demo","unitPrice":99.00,"stock":100}'
```

## 12. WSL2 常见问题

| 问题 | 原因 | 处理 |
|------|------|------|
| `E: Unable to locate package docker-compose-plugin` | 使用了 Ubuntu 默认源，没有 Docker 官方源 | 使用 Docker Desktop，或按本文第 3 节配置 Docker 官方 apt 源 |
| `Cannot connect to the Docker daemon` | Docker Desktop 没启动，或 WSL integration 未开启 | 启动 Docker Desktop，并检查 `Resources -> WSL integration` |
| `docker: command not found` | Ubuntu 内没有 Docker CLI，Docker Desktop 未注入命令 | 开启 Docker Desktop WSL integration 后重开 Ubuntu |
| `systemctl: command not found` 或不可用 | WSL systemd 未启用 | 按第 3 节配置 `/etc/wsl.conf` 后执行 `wsl --shutdown` |
| Windows 浏览器访问不到服务 | 服务没启动或端口没监听 | 在 Ubuntu 里执行 `ss -lntp` 和 `docker ps` |
| Maven 构建很慢 | 项目在 `/mnt/d` 或依赖下载慢 | 推荐放到 `~/projects`，并检查 Maven 网络 |
| Elasticsearch 容器退出 | `vm.max_map_count` 太小 | 执行 `sudo sysctl -w vm.max_map_count=262144` |
| Redis 连接失败 | 密码和配置不一致 | 本文 compose 使用 `root`，服务配置也应使用 `root` |
| MySQL 连接失败 | 容器未就绪或数据库未初始化 | 等待 MySQL 启动后重新执行初始化脚本 |

## 13. 停止和清理

停止 Java 服务：

```bash
pkill -f spring-boot:run
```

停止中间件：

```bash
docker compose -f docker/docker-compose.middleware.yml down
```

删除中间件数据卷：

```bash
docker compose -f docker/docker-compose.middleware.yml down -v
```

注意：`down -v` 会删除 MySQL、Redis、MongoDB、Elasticsearch 的本地数据。执行后需要重新初始化数据库。

## 14. 参考资料

- Docker Engine Ubuntu 官方安装文档：https://docs.docker.com/engine/install/ubuntu/
- Docker Desktop WSL 文档：https://docs.docker.com/desktop/features/wsl/
