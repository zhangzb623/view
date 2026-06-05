# Admin Service

日志与审计后台服务，使用 MongoDB 存储操作日志、审计日志和异常日志，并提供统一查询与统计接口。

## 功能列表

- 操作日志写入与查询
- 审计日志写入与查询
- 异常日志写入与查询
- 日志统计接口
  - 总览统计
  - 异常趋势
  - 服务日志排行

## MongoDB Collections

- `operation_logs`
- `audit_logs`
- `error_logs`

## API 列表

### 写入接口
- `POST /api/admin/logs/operation`
- `POST /api/admin/logs/audit`
- `POST /api/admin/logs/error`

### 查询接口
- `GET /api/admin/logs/operation`
- `GET /api/admin/logs/audit`
- `GET /api/admin/logs/error`

### 统计接口
- `GET /api/admin/logs/statistics/overview`
- `GET /api/admin/logs/statistics/error-trend`
- `GET /api/admin/logs/statistics/service-rank`

## API 示例

### 写入操作日志
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

### 写入审计日志
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

### 写入异常日志
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

### 查询统计总览
```bash
curl -X GET http://localhost:8088/api/admin/logs/statistics/overview
```
