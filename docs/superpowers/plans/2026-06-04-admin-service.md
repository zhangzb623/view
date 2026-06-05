# Admin Service Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build an Admin Service that stores operation, audit, and error logs in MongoDB and exposes write, query, and statistics APIs for replay and troubleshooting.

**Architecture:** The service follows the same Spring Boot module layout already used in the repository and uses Spring Data MongoDB for log persistence. Phase 1 uses HTTP write APIs plus MongoDB query/statistics APIs; the code structure reserves a `consumer` package for later Kafka/RocketMQ ingestion without changing the MongoDB model.

**Tech Stack:** Spring Boot, Spring Cloud, Spring Data MongoDB, Lombok, Jakarta Validation, OpenAPI/Swagger, common-api Result/PageResult wrappers.

---

## File Structure

### New module files
- Create: `admin-service/pom.xml` — module dependencies and Spring Boot packaging
- Create: `admin-service/src/main/resources/application.yml` — MongoDB, server, and logging config
- Create: `admin-service/src/main/resources/bootstrap.yml` — Nacos registration config
- Create: `admin-service/src/main/java/com/learning/admin/AdminServiceApplication.java` — service bootstrap

### Document model files
- Create: `admin-service/src/main/java/com/learning/admin/document/OperationLogDO.java` — operation log Mongo document
- Create: `admin-service/src/main/java/com/learning/admin/document/AuditLogDO.java` — audit log Mongo document
- Create: `admin-service/src/main/java/com/learning/admin/document/ErrorLogDO.java` — error log Mongo document

### DTO files
- Create: `admin-service/src/main/java/com/learning/admin/dto/CreateOperationLogRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/CreateAuditLogRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/CreateErrorLogRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/OperationLogQueryRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/AuditLogQueryRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/ErrorLogQueryRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/OperationLogDTO.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/AuditLogDTO.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/ErrorLogDTO.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/LogOverviewStatisticsDTO.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/ErrorTrendPointDTO.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/ServiceLogRankDTO.java`

### Repository files
- Create: `admin-service/src/main/java/com/learning/admin/repository/OperationLogRepository.java`
- Create: `admin-service/src/main/java/com/learning/admin/repository/AuditLogRepository.java`
- Create: `admin-service/src/main/java/com/learning/admin/repository/ErrorLogRepository.java`

### Service files
- Create: `admin-service/src/main/java/com/learning/admin/service/OperationLogService.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/AuditLogService.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/ErrorLogService.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/StatisticsService.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/impl/OperationLogServiceImpl.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/impl/AuditLogServiceImpl.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/impl/ErrorLogServiceImpl.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/impl/StatisticsServiceImpl.java`

### Controller files
- Create: `admin-service/src/main/java/com/learning/admin/controller/OperationLogController.java`
- Create: `admin-service/src/main/java/com/learning/admin/controller/AuditLogController.java`
- Create: `admin-service/src/main/java/com/learning/admin/controller/ErrorLogController.java`
- Create: `admin-service/src/main/java/com/learning/admin/controller/StatisticsController.java`

### Documentation files
- Create: `admin-service/README.md`
- Modify: `README.md` — add Admin Service progress and quick-start examples

---

### Task 1: Create Admin Service module skeleton

**Files:**
- Create: `admin-service/pom.xml`
- Create: `admin-service/src/main/resources/application.yml`
- Create: `admin-service/src/main/resources/bootstrap.yml`
- Create: `admin-service/src/main/java/com/learning/admin/AdminServiceApplication.java`
- Modify: `pom.xml`

- [ ] **Step 1: Write the failing module bootstrap expectation**

Document the expected module layout in `admin-service/README.md` draft notes before writing code:

```text
admin-service/
  pom.xml
  src/main/java/com/learning/admin/AdminServiceApplication.java
  src/main/resources/application.yml
  src/main/resources/bootstrap.yml
```

- [ ] **Step 2: Add the module to the parent POM**

Add `admin-service` to the `<modules>` section in `pom.xml`.

```xml
<modules>
    <module>common</module>
    <module>gateway/gateway-service</module>
    <module>user-service</module>
    <module>product-service</module>
    <module>order-service</module>
    <module>payment-service</module>
    <module>message-service</module>
    <module>scheduler-service</module>
    <module>admin-service</module>
</modules>
```

- [ ] **Step 3: Create `admin-service/pom.xml`**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>com.learning</groupId>
        <artifactId>spring-cloud-learning-system</artifactId>
        <version>1.0.0</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <artifactId>admin-service</artifactId>
    <name>Admin Service</name>
    <description>日志与审计后台服务</description>

    <dependencies>
        <dependency>
            <groupId>com.learning</groupId>
            <artifactId>common-starter</artifactId>
            <version>1.0.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
    </dependencies>
</project>
```

- [ ] **Step 4: Create bootstrap class and config files**

`admin-service/src/main/java/com/learning/admin/AdminServiceApplication.java`

```java
package com.learning.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.learning")
public class AdminServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }
}
```

`admin-service/src/main/resources/bootstrap.yml`

```yaml
spring:
  application:
    name: admin-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
        namespace: learning-system
        group: LEARNING_GROUP
        service: admin-service
  profiles:
    active: dev
```

`admin-service/src/main/resources/application.yml`

```yaml
server:
  port: 8088

spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/learning_admin
      database: learning_admin

logging:
  level:
    com.learning.admin: DEBUG
```

- [ ] **Step 5: Run module compile check**

Run: `mvn -pl admin-service -am compile`
Expected: build reaches Admin Service and resolves Spring Boot + MongoDB dependencies.

- [ ] **Step 6: Commit**

```bash
git add pom.xml admin-service/pom.xml admin-service/src/main/java/com/learning/admin/AdminServiceApplication.java admin-service/src/main/resources/application.yml admin-service/src/main/resources/bootstrap.yml
git commit -m "feat: scaffold admin service module"
```

### Task 2: Add MongoDB document models

**Files:**
- Create: `admin-service/src/main/java/com/learning/admin/document/OperationLogDO.java`
- Create: `admin-service/src/main/java/com/learning/admin/document/AuditLogDO.java`
- Create: `admin-service/src/main/java/com/learning/admin/document/ErrorLogDO.java`

- [ ] **Step 1: Write the failing shape expectation as comments in a scratch note**

Use these expected field sets while implementing:

```text
OperationLogDO: logId, serviceName, operatorId, operatorName, operationType, businessType, businessId, requestPath, requestMethod, requestParam, resultStatus, resultMessage, ip, createTime
AuditLogDO: auditId, serviceName, businessType, businessId, beforeStatus, afterStatus, action, reason, operatorId, traceId, createTime
ErrorLogDO: errorId, serviceName, businessType, businessId, errorCode, errorMessage, stackSummary, traceId, severity, createTime
```

- [ ] **Step 2: Create `OperationLogDO.java`**

```java
package com.learning.admin.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "operation_logs")
@CompoundIndex(name = "idx_operation_service_time", def = "{'serviceName': 1, 'createTime': -1}")
public class OperationLogDO {

    @Id
    private String logId;
    private String serviceName;
    private Long operatorId;
    private String operatorName;
    private String operationType;
    private String businessType;
    private String businessId;
    private String requestPath;
    private String requestMethod;
    private String requestParam;
    private Integer resultStatus;
    private String resultMessage;
    private String ip;
    private LocalDateTime createTime;
}
```

- [ ] **Step 3: Create `AuditLogDO.java`**

```java
package com.learning.admin.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "audit_logs")
@CompoundIndex(name = "idx_audit_business_time", def = "{'businessType': 1, 'businessId': 1, 'createTime': -1}")
public class AuditLogDO {

    @Id
    private String auditId;
    private String serviceName;
    private String businessType;
    private String businessId;
    private String beforeStatus;
    private String afterStatus;
    private String action;
    private String reason;
    private Long operatorId;
    private String traceId;
    private LocalDateTime createTime;
}
```

- [ ] **Step 4: Create `ErrorLogDO.java`**

```java
package com.learning.admin.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "error_logs")
@CompoundIndex(name = "idx_error_service_time", def = "{'serviceName': 1, 'createTime': -1}")
public class ErrorLogDO {

    @Id
    private String errorId;
    private String serviceName;
    private String businessType;
    private String businessId;
    private String errorCode;
    private String errorMessage;
    private String stackSummary;
    private String traceId;
    private String severity;
    private LocalDateTime createTime;
}
```

- [ ] **Step 5: Run compile check**

Run: `mvn -pl admin-service -am compile`
Expected: document classes compile and Spring Data MongoDB annotations resolve.

- [ ] **Step 6: Commit**

```bash
git add admin-service/src/main/java/com/learning/admin/document/*.java
git commit -m "feat: add admin log document models"
```

### Task 3: Add request, query, response, and statistics DTOs

**Files:**
- Create: `admin-service/src/main/java/com/learning/admin/dto/CreateOperationLogRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/CreateAuditLogRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/CreateErrorLogRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/OperationLogQueryRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/AuditLogQueryRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/ErrorLogQueryRequest.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/OperationLogDTO.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/AuditLogDTO.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/ErrorLogDTO.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/LogOverviewStatisticsDTO.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/ErrorTrendPointDTO.java`
- Create: `admin-service/src/main/java/com/learning/admin/dto/ServiceLogRankDTO.java`

- [ ] **Step 1: Create create-request DTOs with validation**

`CreateOperationLogRequest.java`

```java
package com.learning.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateOperationLogRequest {

    @NotBlank(message = "serviceName不能为空")
    private String serviceName;
    private Long operatorId;
    private String operatorName;
    @NotBlank(message = "operationType不能为空")
    private String operationType;
    @NotBlank(message = "businessType不能为空")
    private String businessType;
    private String businessId;
    private String requestPath;
    private String requestMethod;
    private String requestParam;
    private Integer resultStatus;
    private String resultMessage;
    private String ip;
}
```

`CreateAuditLogRequest.java`

```java
package com.learning.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateAuditLogRequest {

    @NotBlank(message = "serviceName不能为空")
    private String serviceName;
    @NotBlank(message = "businessType不能为空")
    private String businessType;
    @NotBlank(message = "businessId不能为空")
    private String businessId;
    private String beforeStatus;
    private String afterStatus;
    @NotBlank(message = "action不能为空")
    private String action;
    private String reason;
    private Long operatorId;
    private String traceId;
}
```

`CreateErrorLogRequest.java`

```java
package com.learning.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateErrorLogRequest {

    @NotBlank(message = "serviceName不能为空")
    private String serviceName;
    @NotBlank(message = "businessType不能为空")
    private String businessType;
    private String businessId;
    private String errorCode;
    @NotBlank(message = "errorMessage不能为空")
    private String errorMessage;
    private String stackSummary;
    private String traceId;
    @NotBlank(message = "severity不能为空")
    private String severity;
}
```

- [ ] **Step 2: Create query DTOs**

`OperationLogQueryRequest.java`

```java
package com.learning.admin.dto;

import lombok.Data;

@Data
public class OperationLogQueryRequest {
    private String serviceName;
    private Long operatorId;
    private String businessType;
    private String businessId;
    private String startTime;
    private String endTime;
    private Integer current = 1;
    private Integer size = 10;
}
```

`AuditLogQueryRequest.java`

```java
package com.learning.admin.dto;

import lombok.Data;

@Data
public class AuditLogQueryRequest {
    private String serviceName;
    private String businessType;
    private String businessId;
    private String action;
    private String startTime;
    private String endTime;
    private Integer current = 1;
    private Integer size = 10;
}
```

`ErrorLogQueryRequest.java`

```java
package com.learning.admin.dto;

import lombok.Data;

@Data
public class ErrorLogQueryRequest {
    private String serviceName;
    private String severity;
    private String businessType;
    private String startTime;
    private String endTime;
    private Integer current = 1;
    private Integer size = 10;
}
```

- [ ] **Step 3: Create response DTOs and statistics DTOs**

Use one-to-one field copies for response DTOs. Use these exact statistics DTO shapes:

`LogOverviewStatisticsDTO.java`

```java
package com.learning.admin.dto;

import lombok.Data;

@Data
public class LogOverviewStatisticsDTO {
    private long operationLogCount;
    private long auditLogCount;
    private long errorLogCount;
    private long recent24hErrorCount;
}
```

`ErrorTrendPointDTO.java`

```java
package com.learning.admin.dto;

import lombok.Data;

@Data
public class ErrorTrendPointDTO {
    private String timeBucket;
    private long count;
}
```

`ServiceLogRankDTO.java`

```java
package com.learning.admin.dto;

import lombok.Data;

@Data
public class ServiceLogRankDTO {
    private String serviceName;
    private long totalCount;
    private long errorCount;
}
```

- [ ] **Step 4: Run compile check**

Run: `mvn -pl admin-service -am compile`
Expected: DTO package compiles and Jakarta Validation annotations resolve.

- [ ] **Step 5: Commit**

```bash
git add admin-service/src/main/java/com/learning/admin/dto/*.java
git commit -m "feat: add admin service dto models"
```

### Task 4: Add repositories for MongoDB access

**Files:**
- Create: `admin-service/src/main/java/com/learning/admin/repository/OperationLogRepository.java`
- Create: `admin-service/src/main/java/com/learning/admin/repository/AuditLogRepository.java`
- Create: `admin-service/src/main/java/com/learning/admin/repository/ErrorLogRepository.java`

- [ ] **Step 1: Create `OperationLogRepository.java`**

```java
package com.learning.admin.repository;

import com.learning.admin.document.OperationLogDO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OperationLogRepository extends MongoRepository<OperationLogDO, String> {
}
```

- [ ] **Step 2: Create `AuditLogRepository.java`**

```java
package com.learning.admin.repository;

import com.learning.admin.document.AuditLogDO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLogDO, String> {
}
```

- [ ] **Step 3: Create `ErrorLogRepository.java`**

```java
package com.learning.admin.repository;

import com.learning.admin.document.ErrorLogDO;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ErrorLogRepository extends MongoRepository<ErrorLogDO, String> {
}
```

- [ ] **Step 4: Run compile check**

Run: `mvn -pl admin-service -am compile`
Expected: repository interfaces compile and bind to MongoDB document types.

- [ ] **Step 5: Commit**

```bash
git add admin-service/src/main/java/com/learning/admin/repository/*.java
git commit -m "feat: add admin mongo repositories"
```

### Task 5: Implement operation log write and query service

**Files:**
- Create: `admin-service/src/main/java/com/learning/admin/service/OperationLogService.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/impl/OperationLogServiceImpl.java`

- [ ] **Step 1: Define service interface**

```java
package com.learning.admin.service;

import com.learning.admin.dto.CreateOperationLogRequest;
import com.learning.admin.dto.OperationLogDTO;
import com.learning.admin.dto.OperationLogQueryRequest;
import com.learning.common.api.result.PageResult;

public interface OperationLogService {
    void createLog(CreateOperationLogRequest request);
    PageResult<OperationLogDTO> pageLogs(OperationLogQueryRequest request);
}
```

- [ ] **Step 2: Implement minimal write logic**

```java
@Override
public void createLog(CreateOperationLogRequest request) {
    OperationLogDO log = new OperationLogDO();
    BeanUtils.copyProperties(request, log);
    log.setCreateTime(LocalDateTime.now());
    operationLogRepository.save(log);
}
```

- [ ] **Step 3: Implement paged query with `MongoTemplate`**

Use these exact query-building rules:

```java
Query query = new Query();
if (StringUtils.hasText(request.getServiceName())) {
    query.addCriteria(Criteria.where("serviceName").is(request.getServiceName()));
}
if (request.getOperatorId() != null) {
    query.addCriteria(Criteria.where("operatorId").is(request.getOperatorId()));
}
if (StringUtils.hasText(request.getBusinessType())) {
    query.addCriteria(Criteria.where("businessType").is(request.getBusinessType()));
}
if (StringUtils.hasText(request.getBusinessId())) {
    query.addCriteria(Criteria.where("businessId").is(request.getBusinessId()));
}
query.with(Sort.by(Sort.Direction.DESC, "createTime"));
query.skip((long) (request.getCurrent() - 1) * request.getSize());
query.limit(request.getSize());
```

- [ ] **Step 4: Map results into `PageResult<OperationLogDTO>`**

```java
long total = mongoTemplate.count(countQuery, OperationLogDO.class);
List<OperationLogDTO> records = logs.stream().map(this::toDTO).toList();
return PageResult.of(records, total, request.getCurrent(), request.getSize());
```

- [ ] **Step 5: Run compile check**

Run: `mvn -pl admin-service -am compile`
Expected: operation log service compiles with Mongo query support.

- [ ] **Step 6: Commit**

```bash
git add admin-service/src/main/java/com/learning/admin/service/OperationLogService.java admin-service/src/main/java/com/learning/admin/service/impl/OperationLogServiceImpl.java
git commit -m "feat: implement operation log service"
```

### Task 6: Implement audit and error log services

**Files:**
- Create: `admin-service/src/main/java/com/learning/admin/service/AuditLogService.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/ErrorLogService.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/impl/AuditLogServiceImpl.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/impl/ErrorLogServiceImpl.java`

- [ ] **Step 1: Define service interfaces**

Use the same shape as operation logs, with the proper DTO types:

```java
public interface AuditLogService {
    void createLog(CreateAuditLogRequest request);
    PageResult<AuditLogDTO> pageLogs(AuditLogQueryRequest request);
}

public interface ErrorLogService {
    void createLog(CreateErrorLogRequest request);
    PageResult<ErrorLogDTO> pageLogs(ErrorLogQueryRequest request);
}
```

- [ ] **Step 2: Implement audit log write/query**

Write path:

```java
AuditLogDO log = new AuditLogDO();
BeanUtils.copyProperties(request, log);
log.setCreateTime(LocalDateTime.now());
auditLogRepository.save(log);
```

Query filters must include:
- `serviceName`
- `businessType`
- `businessId`
- `action`
- time range

- [ ] **Step 3: Implement error log write/query**

Write path:

```java
ErrorLogDO log = new ErrorLogDO();
BeanUtils.copyProperties(request, log);
log.setCreateTime(LocalDateTime.now());
errorLogRepository.save(log);
```

Query filters must include:
- `serviceName`
- `severity`
- `businessType`
- time range

- [ ] **Step 4: Run compile check**

Run: `mvn -pl admin-service -am compile`
Expected: audit and error log services compile.

- [ ] **Step 5: Commit**

```bash
git add admin-service/src/main/java/com/learning/admin/service/AuditLogService.java admin-service/src/main/java/com/learning/admin/service/ErrorLogService.java admin-service/src/main/java/com/learning/admin/service/impl/AuditLogServiceImpl.java admin-service/src/main/java/com/learning/admin/service/impl/ErrorLogServiceImpl.java
git commit -m "feat: implement audit and error log services"
```

### Task 7: Implement statistics service

**Files:**
- Create: `admin-service/src/main/java/com/learning/admin/service/StatisticsService.java`
- Create: `admin-service/src/main/java/com/learning/admin/service/impl/StatisticsServiceImpl.java`

- [ ] **Step 1: Define statistics service interface**

```java
package com.learning.admin.service;

import com.learning.admin.dto.ErrorTrendPointDTO;
import com.learning.admin.dto.LogOverviewStatisticsDTO;
import com.learning.admin.dto.ServiceLogRankDTO;

import java.util.List;

public interface StatisticsService {
    LogOverviewStatisticsDTO getOverview();
    List<ErrorTrendPointDTO> getErrorTrend();
    List<ServiceLogRankDTO> getServiceRank();
}
```

- [ ] **Step 2: Implement overview statistics**

```java
LogOverviewStatisticsDTO dto = new LogOverviewStatisticsDTO();
dto.setOperationLogCount(operationLogRepository.count());
dto.setAuditLogCount(auditLogRepository.count());
dto.setErrorLogCount(errorLogRepository.count());

dto.setRecent24hErrorCount(
    mongoTemplate.count(
        Query.query(Criteria.where("createTime").gte(LocalDateTime.now().minusHours(24))),
        ErrorLogDO.class
    )
);
return dto;
```

- [ ] **Step 3: Implement error trend aggregation**

Use hourly buckets over the recent 24 hours with a Mongo aggregation pipeline.

```java
Aggregation aggregation = Aggregation.newAggregation(
    Aggregation.match(Criteria.where("createTime").gte(LocalDateTime.now().minusHours(24))),
    Aggregation.project().andExpression("dateToString('%Y-%m-%d %H:00:00', createTime)").as("timeBucket"),
    Aggregation.group("timeBucket").count().as("count"),
    Aggregation.sort(Sort.by(Sort.Direction.ASC, "_id"))
);
```

- [ ] **Step 4: Implement service ranking aggregation**

Use a per-service grouping over all three collections, then merge counts in Java:

```java
Map<String, Long> operationCounts = ...;
Map<String, Long> errorCounts = ...;
ServiceLogRankDTO dto = new ServiceLogRankDTO();
dto.setServiceName(serviceName);
dto.setTotalCount(operationCount + auditCount + errorCount);
dto.setErrorCount(errorCount);
```

- [ ] **Step 5: Run compile check**

Run: `mvn -pl admin-service -am compile`
Expected: aggregation code compiles.

- [ ] **Step 6: Commit**

```bash
git add admin-service/src/main/java/com/learning/admin/service/StatisticsService.java admin-service/src/main/java/com/learning/admin/service/impl/StatisticsServiceImpl.java
git commit -m "feat: add admin log statistics service"
```

### Task 8: Expose controllers for write and query APIs

**Files:**
- Create: `admin-service/src/main/java/com/learning/admin/controller/OperationLogController.java`
- Create: `admin-service/src/main/java/com/learning/admin/controller/AuditLogController.java`
- Create: `admin-service/src/main/java/com/learning/admin/controller/ErrorLogController.java`
- Create: `admin-service/src/main/java/com/learning/admin/controller/StatisticsController.java`

- [ ] **Step 1: Create operation log controller**

```java
package com.learning.admin.controller;

import com.learning.admin.dto.CreateOperationLogRequest;
import com.learning.admin.dto.OperationLogDTO;
import com.learning.admin.dto.OperationLogQueryRequest;
import com.learning.admin.service.OperationLogService;
import com.learning.common.api.result.PageResult;
import com.learning.common.api.result.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/logs/operation")
public class OperationLogController {

    private final OperationLogService operationLogService;

    public OperationLogController(OperationLogService operationLogService) {
        this.operationLogService = operationLogService;
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody CreateOperationLogRequest request) {
        operationLogService.createLog(request);
        return Result.success();
    }

    @GetMapping
    public Result<PageResult<OperationLogDTO>> page(OperationLogQueryRequest request) {
        return Result.success(operationLogService.pageLogs(request));
    }
}
```

- [ ] **Step 2: Create audit and error controllers**

Use the same structure with these mappings:
- `@RequestMapping("/api/admin/logs/audit")`
- `@RequestMapping("/api/admin/logs/error")`

- [ ] **Step 3: Create statistics controller**

```java
@RestController
@RequestMapping("/api/admin/logs/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/overview")
    public Result<LogOverviewStatisticsDTO> overview() {
        return Result.success(statisticsService.getOverview());
    }

    @GetMapping("/error-trend")
    public Result<List<ErrorTrendPointDTO>> errorTrend() {
        return Result.success(statisticsService.getErrorTrend());
    }

    @GetMapping("/service-rank")
    public Result<List<ServiceLogRankDTO>> serviceRank() {
        return Result.success(statisticsService.getServiceRank());
    }
}
```

- [ ] **Step 4: Run application compile check**

Run: `mvn -pl admin-service -am compile`
Expected: all controllers compile and wire successfully.

- [ ] **Step 5: Commit**

```bash
git add admin-service/src/main/java/com/learning/admin/controller/*.java
git commit -m "feat: expose admin log APIs"
```

### Task 9: Document Admin Service and update root README

**Files:**
- Create: `admin-service/README.md`
- Modify: `README.md`

- [ ] **Step 1: Create `admin-service/README.md`**

Include these sections:

```markdown
# Admin Service

## 功能列表
- 操作日志写入与查询
- 审计日志写入与查询
- 异常日志写入与查询
- 日志统计接口

## MongoDB Collections
- operation_logs
- audit_logs
- error_logs

## API示例
- POST /api/admin/logs/operation
- POST /api/admin/logs/audit
- POST /api/admin/logs/error
- GET /api/admin/logs/statistics/overview
```
```

- [ ] **Step 2: Update `README.md` status and project structure**

Add Admin Service in the same style as other modules:

```markdown
- ✅ Admin Service - 100%
  - ✅ MongoDB document models
  - ✅ write APIs
  - ✅ query APIs
  - ✅ statistics APIs
  - ✅ README.md
```

Add module tree entry:

```markdown
├── admin-service/                   # 管理服务 ✅ 100%
```

- [ ] **Step 3: Add quick-start API examples**

Include examples for:
- write operation log
- write audit log
- write error log
- query overview statistics

- [ ] **Step 4: Commit**

```bash
git add admin-service/README.md README.md
git commit -m "docs: add admin service documentation"
```

### Task 10: Manual verification of core flows

**Files:**
- Test: `admin-service/src/main/java/com/learning/admin/controller/*.java`
- Test: `admin-service/src/main/java/com/learning/admin/service/impl/*.java`
- Test: `admin-service/src/main/resources/application.yml`

- [ ] **Step 1: Start MongoDB if needed**

Run: `docker run -d --name learning-mongo -p 27017:27017 mongo:6.0`
Expected: MongoDB is reachable on `localhost:27017`.

- [ ] **Step 2: Start Admin Service**

Run: `mvn -pl admin-service spring-boot:run`
Expected: application starts on port `8088` and connects to MongoDB.

- [ ] **Step 3: Verify operation log write and query**

Run:

```bash
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
```

Expected: success response.

Then run:

```bash
curl "http://localhost:8088/api/admin/logs/operation?serviceName=order-service&current=1&size=10"
```

Expected: one matching record in `records`.

- [ ] **Step 4: Verify audit log write and query**

Run:

```bash
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
```

Expected: success response.

Then run:

```bash
curl "http://localhost:8088/api/admin/logs/audit?businessId=2001&current=1&size=10"
```

Expected: one matching record in `records`.

- [ ] **Step 5: Verify error log write and statistics**

Run:

```bash
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
```

Expected: success response.

Then run:

```bash
curl http://localhost:8088/api/admin/logs/statistics/overview
```

Expected: counts for operation, audit, and error logs are all greater than or equal to 1.

- [ ] **Step 6: Commit**

```bash
git add .
git commit -m "test: verify admin service core log flows"
```

## Self-Review

### Spec coverage
- HTTP write APIs: covered in Task 8
- MongoDB collections and repositories: covered in Tasks 2 and 4
- paginated query APIs: covered in Tasks 5, 6, and 8
- basic statistics APIs: covered in Tasks 7 and 8
- future consumer extension point: preserved in file structure and docs, intentionally not implemented in phase 1 per spec

### Placeholder scan
- Removed TODO-style execution steps from the plan body
- All code-edit steps include concrete code blocks
- All verification steps include exact commands and expected outcomes

### Type consistency
- `createLog(...)` methods consistently return `void`
- all page methods consistently return `PageResult<...>`
- statistics method names match controller calls
- request/response DTO names are consistent across controllers and services
