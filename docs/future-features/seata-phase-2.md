# Seata Phase 2

This document collects distributed transaction capabilities that should be implemented after the current service baseline is stable.

## Current iteration boundary

The current iteration can introduce Seata as a learning target, but it does not need a full production-grade distributed transaction rollout yet.

The baseline should stay focused on:
- understanding transaction boundaries between services
- preparing realistic business tables and service dependencies
- keeping local transactions clear inside each service

Anything below should remain deferred until the core service workflows are stable.

## Phase 2 backlog

### 1. End-to-end order transaction orchestration
- place order across order, product, and payment services
- stock reservation and rollback path
- payment status rollback coordination
- failure scenario replay scripts

Reason to postpone:
- This needs all participating services to be stable first.

Priority: P1

### 2. Seata server deployment standardization
- standalone Seata server deployment
- registry and config integration
- service startup templates
- health-check procedures

Reason to postpone:
- Infrastructure standardization should follow after the business flow is confirmed.

Priority: P1

### 3. AT mode rollout
- undo_log preparation
- datasource proxy integration
- rollback validation for order flows
- contention test cases

Reason to postpone:
- It adds invasive data-source behavior that is better introduced after local transactions are already correct.

Priority: P1

### 4. TCC exploration for payment-sensitive paths
- try/confirm/cancel API design
- payment freeze record model
- timeout compensation handling
- idempotency rules

Reason to postpone:
- TCC is more advanced than the current first-pass learning slice.

Priority: P2

### 5. Saga-based long transaction demo
- order lifecycle orchestration
- message-driven compensation chain
- visible state transitions
- failure replay examples

Reason to postpone:
- Saga is valuable for learning, but it should not be mixed into the first transactional baseline.

Priority: P2

### 6. Transaction observability
- global transaction id tracing
- branch transaction logs
- rollback metrics
- failed compensation dashboard data

Reason to postpone:
- Observability work is most useful after the transaction engine is actually introduced.

Priority: P2

## Promotion rule

Move a Seata item into the main implementation only when:
- the participating business services already work in their local transaction model,
- the distributed boundary is explicitly defined,
- and the rollback behavior can be verified with concrete failure cases.
