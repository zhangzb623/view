# Ubuntu Server Deployment Document Plan

> **Goal:** Write a copy-paste-ready deployment guide for the Spring Cloud Learning System on a freshly installed Ubuntu 22.04 LTS Server.

**Architecture:** Hybrid deployment — middleware via Docker Compose, Java services run on host via `mvn spring-boot:run`, with `systemd` as the recommended long-running method. The document covers only the currently completed modules.

**Tech Stack:** Docker + Docker Compose, MySQL 8.0, Redis 7, MongoDB 6.0, Elasticsearch 7.10+, Nacos 2.x, xxl-job 2.4.0, Java 17, Maven 3.8+, Spring Boot 3.2.0.

---

## File Structure

### New document files
- Create: `docs/Ubuntu-Deployment.md` — main deployment document
- Modify: `docker/docker-compose.yml` — add new middleware containers if needed for new services
- Modify: `scripts/README.md` — reference the deployment doc

---

### Task 1: Write environment preparation and base tools sections

**File:**
- Create: `docs/Ubuntu-Deployment.md`

- [ ] **Step 1: Write the document header and introduction**

Include:
- Title: "Ubuntu Server Deployment Guide"
- Short description of what this document covers (Spring Cloud Learning System, single-node, hybrid deployment)
- Link back to the project README
- Target OS: Ubuntu 22.04 LTS
- Minimum resource requirements
- Port table listing all required open ports

- [ ] **Step 2: Write the environment preparation section**

Include:
- Verify Ubuntu version (`lsb_release -a`)
- Check CPU/memory/disk (`lscpu`, `free -h`, `df -h`)
- Open firewall ports (`ufw` commands)
- Acceptable to use `\`continued on next page`

- [ ] **Step 3: Write the base tools installation section**

Include install and verify commands for:
- `git`, `curl`, `vim`
- `docker` (with post-install steps for non-root user)
- `docker compose` (as plugin)
- `openjdk-17-jdk`
- `maven`

Each pair must be: install command → version check command → expected output

- [ ] **Step 4: Commit**

```bash
git add docs/Ubuntu-Deployment.md
git commit -m "docs(deploy): add environment and base tools sections"
```

### Task 2: Write the code acquisition and infrastructure startup sections

**File:**
- Modify: `docs/Ubuntu-Deployment.md`

- [ ] **Step 1: Write the code acquisition section**

Include:
- `git clone` command for the repository
- `cd` into project directory
- Verify the project structure (`ls -la`)
- Confirm key directories: `common/`, `scripts/`, `user-service/`, `product-service/`, etc.

- [ ] **Step 2: Write the infrastructure startup section with docker-compose up**

Verify the `docker/docker-compose.yml` references all needed services and write the startup commands.

Include startup and health check for each of:
1. MySQL (`docker compose up -d mysql`)
2. Redis (`docker compose up -d redis`)
3. MongoDB (`docker compose up -d mongo`)
4. Elasticsearch (`docker compose up -d elasticsearch`)
5. Nacos (`docker compose up -d nacos`)
6. xxl-job admin (`docker compose up -d xxl-job-admin`)

For each service:
- docker compose up command
- wait time (10-30s between services)
- health check command or container state check
- expected output

- [ ] **Step 3: Commit**

```bash
git add docs/Ubuntu-Deployment.md docker/docker-compose.yml
git commit -m "docs(deploy): add infrastructure startup sections"
```

### Task 3: Write the database initialization section

**File:**
- Modify: `docs/Ubuntu-Deployment.md`

- [ ] **Step 1: Write the database initialization steps**

Include:
- Wait for MySQL to be ready (loop check)
- Run `init.sql` (main database)
- Run `xxl-job.sql` (scheduler database)
- Run `seata.sql` (transaction database — schema only)
- Verification SQL queries
- Enter each database (`USE learning_system`) and check tables

- [ ] **Step 2: Commit**

```bash
git add docs/Ubuntu-Deployment.md
git commit -m "docs(deploy): add database initialization section"
```

### Task 4: Write the build and start services section

**File:**
- Modify: `docs/Ubuntu-Deployment.md`

- [ ] **Step 1: Write the common module build step**

Include:
- `cd ~/spring-cloud-learning-system`
- `mvn clean install -pl common/common-domain,common/common-util,common/common-api,common/common-starter -DskipTests`
- Expected: BUILD SUCCESS
- Troubleshooting note for parent POM resolution

- [ ] **Step 2: Write the service startup sections for all completed services**

For each service:
- `mvn spring-boot:run` command (or `mvn spring-boot:run -Dspring-boot.run.arguments=...`)
- Expected startup log line (e.g., "Started UserServiceApplication in X seconds")
- Expected listening port

Services in order:
1. User Service (port 8082)
2. Product Service (port 8083)
3. Order Service (port 8084)
4. Payment Service (port 8085)
5. Message Service (port 8086)
6. Scheduler Service (port 8087)
7. Admin Service (port 8088)

- [ ] **Step 3: Write the systemd service unit template section**

Include a generic `systemd` unit file that can be adapted per service:

```ini
[Unit]
Description=[Service Name]
After=network.target

[Service]
Type=simple
User=ubuntu
WorkingDirectory=/home/ubuntu/spring-cloud-learning-system/[service-name]
ExecStart=/usr/bin/mvn spring-boot:run
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

Also include the `systemctl` commands to enable and start each unit.

- [ ] **Step 4: Commit**

```bash
git add docs/Ubuntu-Deployment.md
git commit -m "docs(deploy): add service build and start section"
```

### Task 5: Write the deployment verification section

**File:**
- Modify: `docs/Ubuntu-Deployment.md`

- [ ] **Step 1: Write verification curl commands per service**

Include these checks:

#### User Service
```bash
# Register
curl -s -X POST http://localhost:8082/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "pass", "phone": "13800138000", "email": "t@t.com"}'

# Login
curl -s -X POST http://localhost:8082/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "pass"}'
```

#### Product Service
```bash
# Create product
curl -s -X POST http://localhost:8083/api/product/create \
  -H "Content-Type: application/json" \
  -d '{"categoryId": 1, "productName": "Demo", "unitPrice": 99.00, "stock": 100}'
```

#### Order Service
```bash
# Create order
curl -s -X POST http://localhost:8084/api/order/create \
  -H "Content-Type: application/json" -H "X-User-Id: 1" \
  -d '{"productId": 1, "productName": "Demo", "quantity": 1, "unitPrice": 99.00, "totalPrice": 99.00, "paymentMethod": 3, "address": "Shenzhen", "receiver": "Zhang", "receiverPhone": "13800138000"}'
```

#### Payment Service
```bash
# Create payment
curl -s -X POST http://localhost:8085/api/payment/create \
  -H "Content-Type: application/json" \
  -d '{"orderId": 1, "userId": 1, "paymentMethod": 3, "amount": 99.00}'
```

#### Message Service
```bash
# Create message
curl -s -X POST http://localhost:8086/api/message/create \
  -H "Content-Type: application/json" \
  -d '{"userId": 1, "messageType": 1, "title": "Test", "content": "test", "important": 0}'
```

#### Admin Service
```bash
# Write operation log
curl -s -X POST http://localhost:8088/api/admin/logs/operation \
  -H "Content-Type: application/json" \
  -d '{"serviceName": "test", "operationType": "TEST", "businessType": "test", "resultStatus": 200, "resultMessage": "ok"}'

# Get statistics
curl -s http://localhost:8088/api/admin/logs/statistics/overview
```

Also include:
- Check Nacos console at `http://<server-ip>:8848/nacos`
- Verify registered services

- [ ] **Step 2: Commit**

```bash
git add docs/Ubuntu-Deployment.md
git commit -m "docs(deploy): add verification section"
```

### Task 6: Write the troubleshooting and cleanup sections

**File:**
- Modify: `docs/Ubuntu-Deployment.md`

- [ ] **Step 1: Write the troubleshooting section**

Cover these issues:

| Issue | Likely Cause | Check & Fix |
|-------|------------|-------------|
| `ssh: connect to host` timeout | Firewall not open | `ufw status`, verify port rules |
| `docker: command not found` | Docker not installed | `which docker`, reinstall |
| `Cannot connect to the Docker daemon` | User not in docker group | `sudo usermod -aG docker $USER` |
| `Java not recognized` | No JDK | `java -version`, install openjdk-17 |
| `Maven build fails: parent POM` | Parent not installed first | `mvn clean install -pl common/...` first |
| `MySQL connection refused` | Container not ready | `docker ps`, wait 30s |
| `Nacos not reachable` | Nacos slow to start | Wait 60s, check logs |
| `Elasticsearch exits with code 78` | Memory limit | `sysctl -w vm.max_map_count=262144` |
| `Port already in use` | Previous service | `ss -lntp`, kill process |

- [ ] **Step 2: Write the teardown and cleanup section**

Include:
```bash
# Stop all services (kill java processes)
pkill -f spring-boot:run

# Stop all containers
docker compose down

# Remove all data volumes (optional)
docker compose down -v
```

- [ ] **Step 3: Commit**

```bash
git add docs/Ubuntu-Deployment.md
git commit -m "docs(deploy): add troubleshooting and cleanup sections"
```

### Task 7: Update docker compose and project docs

**Files:**
- Modify: `docker/docker-compose.yml`
- Modify: `scripts/README.md`
- Modify: `README.md`

- [ ] **Step 1: Review Docker Compose file for new middleware requirements**

Check that `docker/docker-compose.yml` includes:
- `mysql` (port 3306)
- `redis` (port 6379)
- `mongo` (port 27017)
- `elasticsearch` (port 9200, 9300)
- `nacos` (port 8848)
- `xxl-job-admin` (port 8080)

If any are missing from this file, note that they must be added for the deployment document to work end-to-end.

- [ ] **Step 2: Add a cross-reference to the deployment document in README**

Add a line to the project `README.md` Documentation section:

```markdown
- [Ubuntu Server Deployment Guide](docs/Ubuntu-Deployment.md)
```

- [ ] **Step 3: Commit**

```bash
git add docker/docker-compose.yml scripts/README.md README.md
git commit -m "docs: update project docs referencing ubuntu deployment"
```
