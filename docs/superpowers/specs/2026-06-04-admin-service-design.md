# Admin Service Design

## Overview

Admin Service is a read-heavy MongoDB-backed log and audit backend for the Spring Cloud Learning System. It is intentionally separated from core transaction services such as User, Order, Payment, Message, and Scheduler. Its role is to receive, store, query, and summarize logs for learning, replay, and operational review.

This service is designed to demonstrate MongoDB in a production-like but scope-controlled way. It focuses on document modeling, flexible querying, and lightweight statistics rather than real admin UI, complex permissions, or full observability platform features.

## Goals

- Demonstrate MongoDB document modeling for backend logs
- Keep Admin Service isolated from core transaction responsibilities
- Provide a unified backend for operation logs, audit logs, and error logs
- Support paginated queries, filtered queries, and basic statistics
- Fit the existing project style used by User, Product, Order, Payment, Message, and Scheduler services

## Non-Goals

- No frontend admin console in the first iteration
- No complex RBAC or login system in the first iteration
- No full tracing or APM replacement
- No archival or cold-storage pipeline in the first iteration
- No mandatory Kafka/RocketMQ ingestion in the first iteration

## Responsibilities and Boundaries

Admin Service is a read-mostly backend service.

It should:
- accept log write requests
- store structured documents in MongoDB
- provide query APIs for log review
- provide summary/statistics APIs for replay and troubleshooting

It should not:
- create or modify user, order, payment, or message business data
- participate in core trading transactions
- own scheduling or delivery responsibilities already handled by Scheduler or Message services

## Architecture

### Module Structure

Admin Service should follow the same project pattern as the other services:

- `controller`
- `dto`
- `document`
- `repository`
- `service`
- `service/impl`
- `consumer` (reserved for later Kafka/RocketMQ ingestion)
- `src/main/resources`

### Data Flow

Phase 1 data flow:
1. Other services send structured log payloads through HTTP APIs
2. Admin Service validates and stores documents in MongoDB
3. Query/statistics APIs read from MongoDB and return structured results

Phase 2 extension:
1. Other services publish log events to Kafka or RocketMQ
2. Admin Service consumes those events
3. Admin Service stores them into the same MongoDB collections

This keeps phase 1 easy to run while preserving a natural upgrade path toward message-driven ingestion.

## MongoDB Collections

Initial design uses three collections.

### 1. operation_logs

Purpose: capture user or system actions.

Suggested fields:
- `logId`
- `serviceName`
- `operatorId`
- `operatorName`
- `operationType`
- `businessType`
- `businessId`
- `requestPath`
- `requestMethod`
- `requestParam`
- `resultStatus`
- `resultMessage`
- `ip`
- `createTime`

Examples:
- user profile update
- order cancellation
- payment request creation
- manual backend action

### 2. audit_logs

Purpose: capture business state changes.

Suggested fields:
- `auditId`
- `serviceName`
- `businessType`
- `businessId`
- `beforeStatus`
- `afterStatus`
- `action`
- `reason`
- `operatorId`
- `traceId`
- `createTime`

Examples:
- order changed from unpaid to paid
- payment changed from processing to success
- message changed from unread to read

### 3. error_logs

Purpose: capture failures and exceptions.

Suggested fields:
- `errorId`
- `serviceName`
- `businessType`
- `businessId`
- `errorCode`
- `errorMessage`
- `stackSummary`
- `traceId`
- `severity`
- `createTime`

Examples:
- order creation failure
- payment callback failure
- scheduler task exception
- message consumption failure

### Why three collections

This split keeps the first version understandable:
- operation = behavior
- audit = state transition
- error = failure

A single shared collection would make queries and indexes noisy. A larger split would add complexity too early.

## API Design

Admin Service exposes write APIs and query/statistics APIs.

### Write APIs

- `POST /api/admin/logs/operation`
- `POST /api/admin/logs/audit`
- `POST /api/admin/logs/error`

These are the initial integration path so that the service can be run and tested without requiring the event pipeline first.

### Query APIs

#### Operation logs
- `GET /api/admin/logs/operation`
- filters: `serviceName`, `operatorId`, `businessType`, `businessId`, time range, pagination

#### Audit logs
- `GET /api/admin/logs/audit`
- filters: `serviceName`, `businessType`, `businessId`, `action`, time range, pagination

#### Error logs
- `GET /api/admin/logs/error`
- filters: `serviceName`, `severity`, `businessType`, time range, pagination

### Statistics APIs

- `GET /api/admin/logs/statistics/overview`
  - total operation log count
  - total audit log count
  - total error log count
  - recent 24h error count

- `GET /api/admin/logs/statistics/error-trend`
  - time-bucketed error count trend

- `GET /api/admin/logs/statistics/service-rank`
  - service-level log/error ranking

## Persistence and Query Strategy

### Storage

Use Spring Data MongoDB.

- `MongoRepository` for straightforward CRUD-like queries
- `MongoTemplate` for multi-condition filters, time-range queries, and grouped statistics

This matches the typical strengths of MongoDB-backed log systems.

### Indexing

Initial indexes should focus on the main query paths.

Recommended indexes:
- `operation_logs`: `(serviceName, createTime)`, `(businessType, businessId)`, `(operatorId, createTime)`
- `audit_logs`: `(serviceName, createTime)`, `(businessType, businessId)`, `(traceId)`
- `error_logs`: `(serviceName, createTime)`, `(severity, createTime)`, `(businessType, businessId)`, `(traceId)`

## DTO and Response Design

Keep the same response style as the rest of the repository.

- write APIs return `Result<Void>` or `Result<String>` depending on whether an ID is returned
- paginated queries return `Result<PageResult<...>>`
- statistics APIs return `Result<...>` DTOs

Suggested DTO groups:
- create request DTOs for each log type
- query request DTOs for each log type
- response DTOs for operation/audit/error logs
- statistics DTOs for overview, trend, and ranking

## Error Handling

Use the shared common-starter exception/result style.

Validation should happen at the write API boundary:
- required fields must be present
- time ranges must be valid
- severity should be constrained to supported values

Invalid requests should return standard `Result.fail(...)` responses.

## Testing Strategy

Phase 1 verification should cover five main paths:

1. write one operation log and query it successfully
2. write one audit log and query it by `businessId`
3. write one error log and query it by `serviceName` plus time range
4. overview statistics returns correct counts
5. paginated APIs return `records`, `total`, `current`, and `size`

## Rollout Plan

### Phase 1
- HTTP write APIs
- MongoDB collections and repositories
- paginated query APIs
- basic statistics APIs

### Phase 2
- Kafka/RocketMQ consumers for log events
- move other services from direct HTTP logging to async event logging

## Tradeoffs

### Why HTTP first

HTTP write APIs make the service easier to run, test, and understand in a learning project. They also reduce setup coupling while the event pipeline is still being expanded.

### Why not all logs in one collection

One collection would simplify writes but weaken query clarity and indexing strategy. The three-collection model is a better learning and maintenance boundary.

### Why no UI now

A backend-only first version keeps the scope focused on MongoDB and service design rather than frontend work.

## Compatibility with Current Project

This design matches the current repository direction:
- MongoDB is already assigned to Admin and Scheduler responsibilities in the README
- existing modules use controller/service/impl/dto patterns
- Result/PageResult conventions are already established
- later Kafka/RocketMQ integration fits the existing Message and Order patterns

## Future Extensions

After the first version, Admin Service can reasonably add:
- Kafka consumer for logs
- RocketMQ consumer for logs
- cross-service trace lookup by `traceId`
- error trend dashboards
- failure hot-spot rankings
- retention cleanup jobs
- admin UI as a separate frontend project
