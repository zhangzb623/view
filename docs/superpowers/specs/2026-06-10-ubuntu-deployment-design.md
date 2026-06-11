# Ubuntu Server Deployment Design

## Overview

This document describes the deployment strategy for the Spring Cloud Learning System on a freshly installed Ubuntu Server. The goal is to produce an executable, copy-paste-ready guide that allows replaying the learning platform end-to-end, suitable for review and demonstration.

This is not a production-grade deployment. It intentionally avoids high availability, load balancing, TLS termination, CI/CD, and Kubernetes. The focus is on getting the currently completed modules running on one machine with the fewest surprises.

## Goals

- Deploy the project on a single Ubuntu Server with a reproducible procedure
- Demonstrate the completed modules together
- Keep the procedure understandable for learning and review
- Avoid mixing unfinished modules into the main deployment flow

## Non-Goals

- Multi-node or high-availability deployment
- Kubernetes-based deployment
- HTTPS / reverse proxy / domain setup
- CI/CD pipeline
- Production monitoring and alerting
- Full Seata integration deployment
- Chat Server (Netty) deployment

## Target Environment

### OS
- Ubuntu 22.04 LTS (recommended)
- Ubuntu 24.04 LTS is acceptable but should be treated as a later verification target

### Minimum resources
- CPU: 2 cores
- RAM: 8 GB (16 GB recommended)
- Disk: 40 GB free

### Required open ports
- 8848 — Nacos
- 3306 — MySQL
- 6379 — Redis
- 27017 — MongoDB
- 9200 / 9300 — Elasticsearch
- 8080 — xxl-job admin
- 8082 — User Service
- 8083 — Product Service
- 8084 — Order Service
- 8085 — Payment Service
- 8086 — Message Service
- 8087 — Scheduler Service
- 8088 — Admin Service

### Deployment scope
The document should cover only currently completed modules:

- Nacos
- MySQL
- Redis
- MongoDB
- Elasticsearch
- xxl-job admin
- User Service
- Product Service
- Order Service
- Payment Service
- Message Service
- Scheduler Service
- Admin Service

### Out of scope for the main flow
- Seata transaction coordination
- Distribution Service
- Chat Server
- Gateway Service end-to-end routing verification
- Dockerizing the Java services in the primary procedure

## Deployment Strategy

The recommended strategy is **hybrid**:

- Middleware runs via Docker Compose
  - MySQL
  - Redis
  - MongoDB
  - Elasticsearch
  - Nacos
  - xxl-job admin
- Business services run on the Ubuntu host
  - Java 17
  - Maven 3.8+
  - `mvn spring-boot:run` for initial verification
  - `systemd` units as the recommended long-running method

This approach keeps middleware setup consistent while keeping Java service behavior visible during learning and debugging.

## Document Structure

The deployment guide should contain these chapters:

### 1. Environment preparation
- Target Ubuntu version
- Resource recommendations
- Port checklist
- Scope summary

### 2. Install base tools
- `git`
- `curl`
- `vim` or `nano`
- `docker`
- `docker compose`
- `openjdk-17-jdk`
- `maven`

Each item should include:
- install command
- version verification command

### 3. Get the project code
- `git clone` instructions
- enter project directory
- confirm `README.md`, `scripts/`, `common/`, and service modules exist

### 4. Start infrastructure
Startup order:

1. MySQL
2. Redis
3. MongoDB
4. Elasticsearch
5. Nacos
6. xxl-job admin

Each step should include:
- startup command
- health check command
- expected result

### 5. Initialize databases
Execute:

- `scripts/mysql/init.sql`
- `scripts/mysql/xxl-job.sql`
- `scripts/mysql/seata.sql` (only for schema creation)

Include verification SQL:
- confirm `learning_system`, `xxl_job`, `seata` databases exist
- confirm key tables exist

### 6. Build and start services
Sequence:

1. Build common modules
2. User Service
3. Product Service
4. Order Service
5. Payment Service
6. Message Service
7. Scheduler Service
8. Admin Service

For each service:
- build/start command
- expected startup log lines
- listening port

Recommended long-running method:
- systemd service unit per service
- include a sample unit template

### 7. Verify deployment success
Provide curl checks:

- User Service: register and login
- Product Service: create product and search
- Order Service: create order and pay
- Payment Service: create payment
- Message Service: create message
- Admin Service: write log and fetch statistics overview

### 8. Troubleshooting
Cover common issues:

- wrong Java version
- unresolved parent POM
- Docker not running
- MongoDB or Elasticsearch unreachable
- Nacos registration failure
- port conflict
- Maven local cache poisoning parent POM resolution

## Execution Granularity

Every chapter should be written at copy-paste granularity.

Each step must include:

1. Directly executable command
2. Verification command
3. Expected result

The document should clearly label:

- recommended action
- optional action
- verification result
- troubleshooting path

## Writing Style

Use imperative instructions:

- good: "Run this command"
- good: "Verify that the service is listening"
- bad: "The user may consider starting the service"

Include short notes when a step is commonly misunderstood.

Example:

- after `docker compose up -d`, wait 10–30 seconds before health check
- Nacos may need extra time before becoming accessible at `/nacos`

## Future Enhancements

This design leaves room for later additions without changing the core deployment document:

- Dockerize all Java services and unify them under Compose
- Add Seata Server to the infrastructure stack
- Add Gateway Service routing verification
- Add Chat Server deployment
- Add systemd-managed logging rotation
- Add basic health-check dashboards

These should be referenced only as future extensions, not part of the primary procedure.

## Success Criteria

The deployment is successful when:

- all middleware containers are `Up` and reachable
- all completed Java services start without build/runtime errors
- curl verification commands return expected responses
- Nacos shows the completed services as registered instances
- Admin Service can receive a log write and return statistics
