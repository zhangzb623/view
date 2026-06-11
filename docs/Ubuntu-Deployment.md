# Ubuntu Server Deployment Guide

This guide shows how to deploy the Spring Cloud Learning System on a fresh Ubuntu Server. The recommended path is a hybrid setup: infrastructure services run in Docker Compose, while Java services run on the host with Maven or systemd.

Project README: `README.md`

## 1. Environment preparation

### Recommended OS
- Ubuntu 22.04 LTS
- Ubuntu 24.04 LTS should also work, but 22.04 is the reference target for this guide

### Minimum resources
- CPU: 2 cores
- Memory: 8 GB minimum, 16 GB recommended
- Disk: 40 GB free minimum

### Required ports
| Port | Service |
|------|---------|
| 3306 | MySQL |
| 6379 | Redis |
| 8848 | Nacos |
| 9200 | Elasticsearch HTTP |
| 9300 | Elasticsearch transport |
| 27017 | MongoDB |
| 8080 | xxl-job admin |
| 8082 | User Service |
| 8083 | Product Service |
| 8084 | Order Service |
| 8085 | Payment Service |
| 8086 | Message Service |
| 8087 | Scheduler Service |
| 8088 | Admin Service |

### Check the host
```bash
lsb_release -a
lscpu
free -h
df -h
```

Expected:
- Ubuntu is 22.04 or newer
- enough CPU, memory, and disk for the stack

### Open firewall ports
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

Expected:
- the listed ports are allowed
- `ufw status verbose` shows active rules

## 2. Install base tools

### Update system packages
```bash
sudo apt update
sudo apt -y upgrade
```

### Install common utilities
```bash
sudo apt install -y git curl vim unzip ca-certificates gnupg lsb-release
```

Verify:
```bash
git --version
curl --version
vim --version | head -n 1
```

### Install Docker
```bash
sudo apt install -y docker.io docker-compose-plugin
sudo systemctl enable docker
sudo systemctl start docker
sudo usermod -aG docker $USER
newgrp docker
```

Verify:
```bash
docker --version
docker compose version
docker ps
```

Expected:
- Docker daemon is running
- current user can run `docker ps` without sudo after re-login or `newgrp docker`

### Install OpenJDK 17
```bash
sudo apt install -y openjdk-17-jdk
```

Verify:
```bash
java -version
javac -version
```

Expected:
- Java 17 is installed

### Install Maven
```bash
sudo apt install -y maven
```

Verify:
```bash
mvn -version
```

Expected:
- Maven 3.8+ is available

## 3. Get the project code

### Clone the repository
```bash
git clone <your-repo-url> spring-cloud-learning-system
cd spring-cloud-learning-system
```

### Verify project files
```bash
ls -la
ls common
ls scripts
```

Expected:
- `pom.xml` exists at repo root
- `common/`, `scripts/`, and all service directories exist

## 4. Start infrastructure

This guide assumes `docker/docker-compose.yml` contains the middleware stack used by the project.

### Start MySQL
```bash
docker compose up -d mysql
```

Verify:
```bash
docker ps --filter name=mysql
```

Expected:
- container state is `Up`

Wait 15-30 seconds before starting the next service.

### Start Redis
```bash
docker compose up -d redis
```

Verify:
```bash
docker ps --filter name=redis
```

Expected:
- container state is `Up`

### Start MongoDB
```bash
docker compose up -d mongo
```

Verify:
```bash
docker ps --filter name=mongo
```

Expected:
- container state is `Up`

### Start Elasticsearch
Before starting Elasticsearch, set the host kernel parameter if needed:

```bash
sudo sysctl -w vm.max_map_count=262144
```

Then start it:

```bash
docker compose up -d elasticsearch
```

Verify:
```bash
curl -s http://localhost:9200
```

Expected:
- JSON response with cluster name and version

### Start Nacos
```bash
docker compose up -d nacos
```

Verify:
```bash
curl -I http://localhost:8848/nacos
```

Expected:
- HTTP 200 or 302 response after the service is ready

### Start xxl-job admin
```bash
docker compose up -d xxl-job-admin
```

Verify:
```bash
curl -I http://localhost:8080/xxl-job-admin
```

Expected:
- HTTP response from the admin console

## 5. Initialize databases

### Wait for MySQL
```bash
until docker exec mysql mysqladmin ping -h 127.0.0.1 -uroot -proot --silent; do sleep 2; done
```

Expected:
- command exits only when MySQL is ready

### Initialize main database
```bash
docker exec -i mysql mysql -uroot -proot < scripts/mysql/init.sql
```

### Initialize xxl-job database
```bash
docker exec -i mysql mysql -uroot -proot < scripts/mysql/xxl-job.sql
```

### Initialize seata database schema
```bash
docker exec -i mysql mysql -uroot -proot < scripts/mysql/seata.sql
```

### Verify databases and tables
```bash
docker exec -it mysql mysql -uroot -proot -e "SHOW DATABASES;"
docker exec -it mysql mysql -uroot -proot -e "USE learning_system; SHOW TABLES;"
docker exec -it mysql mysql -uroot -proot -e "USE xxl_job; SHOW TABLES;"
docker exec -it mysql mysql -uroot -proot -e "USE seata; SHOW TABLES;"
```

Expected:
- `learning_system`, `xxl_job`, `seata` are present
- service tables from the init scripts exist

## 6. Build and start services

### Build the common modules first
```bash
mvn clean install -pl common/common-domain,common/common-util,common/common-api,common/common-starter -DskipTests
```

Expected:
- build completes successfully
- parent POM resolution works from the project root

If Maven fails to resolve the parent POM, run from the repository root and clear any stale local cache entries for `com.learning:learning-system-parent` before retrying.

### Start User Service
```bash
cd user-service
mvn spring-boot:run
```

Expected:
- `Started UserServiceApplication`
- listening on `8082`

### Start Product Service
```bash
cd ../product-service
mvn spring-boot:run
```

Expected:
- `Started ProductServiceApplication`
- listening on `8083`

### Start Order Service
```bash
cd ../order-service
mvn spring-boot:run
```

Expected:
- `Started OrderServiceApplication`
- listening on `8084`

### Start Payment Service
```bash
cd ../payment-service
mvn spring-boot:run
```

Expected:
- `Started PaymentServiceApplication`
- listening on `8085`

### Start Message Service
```bash
cd ../message-service
mvn spring-boot:run
```

Expected:
- `Started MessageServiceApplication`
- listening on `8086`

### Start Scheduler Service
```bash
cd ../scheduler-service
mvn spring-boot:run
```

Expected:
- `Started SchedulerServiceApplication`
- listening on `8087`

### Start Admin Service
```bash
cd ../admin-service
mvn spring-boot:run
```

Expected:
- `Started AdminServiceApplication`
- listening on `8088`

### Optional: run services with systemd
Use this template for each service.

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

Enable and start:
```bash
sudo systemctl daemon-reload
sudo systemctl enable user-service
sudo systemctl start user-service
sudo systemctl status user-service
```

Repeat the same pattern for the other services by changing `Description`, `WorkingDirectory`, and unit name.

## 7. Verify deployment success

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

### Nacos verification
Open:
```text
http://<server-ip>:8848/nacos
```

Expected:
- completed services appear in the instance list

## 8. Troubleshooting

| Issue | Likely cause | Fix |
|------|--------------|-----|
| `docker: command not found` | Docker not installed | Install Docker and Docker Compose plugin |
| `Cannot connect to the Docker daemon` | Current user not in `docker` group | Run `sudo usermod -aG docker $USER` and re-login |
| Java not found | JDK missing | Install `openjdk-17-jdk` |
| Maven fails on parent POM | Build not run from repo root or stale local cache | Run from repo root, refresh Maven cache, rebuild common modules first |
| MySQL connection refused | Container not ready yet | Wait and re-run health check |
| Nacos not reachable | Container still starting | Wait 30-60 seconds and check logs |
| Elasticsearch exits with code 78 | `vm.max_map_count` too low | Run `sudo sysctl -w vm.max_map_count=262144` |
| Port already in use | Previous process still running | `ss -lntp` then stop the conflicting process |
| MongoDB unreachable | Container not started or port blocked | Check `docker ps` and firewall rules |

### Useful stop/cleanup commands
```bash
pkill -f spring-boot:run
docker compose down
# Optional: remove volumes
docker compose down -v
```

## 9. Notes for future expansion

- Seata Server can be added to the Docker Compose stack later
- Gateway Service can be included once its route rules are finalized
- Chat Server can be deployed separately after Netty implementation
- The Java services can be containerized later if you want an all-Docker deployment
