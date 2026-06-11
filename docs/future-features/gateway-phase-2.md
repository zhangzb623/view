# Gateway Phase 2

This document collects Gateway capabilities that should be implemented after the current gateway routing baseline is stable.

## Current iteration boundary

The current iteration should stay focused on:
- gateway service startup
- basic route forwarding
- service registration and discovery integration
- simple request entry for the demo services

Anything below should remain deferred until the basic gateway path is stable.

## Phase 2 backlog

### 1. Route governance
- richer route predicates
- route priority management
- route metadata conventions
- environment-specific route overlays

Reason to postpone:
- The first learning slice only needs a clean gateway entry point.

Priority: P1

### 2. Rate limiting and anti-brush controls
- IP-based throttling
- user-based throttling
- interface-level quota rules
- Redis-backed distributed counters

Reason to postpone:
- Current demo traffic is small and does not require production-level throttling yet.

Priority: P1

### 3. Unified authentication integration
- JWT or token parsing at the gateway
- user context propagation
- white-list route handling
- standard unauthorized response model

Reason to postpone:
- Gateway auth should be added after the downstream service contracts are stable.

Priority: P1

### 4. Gateway observability
- structured access logs
- request latency metrics
- trace id propagation
- slow-route diagnostics

Reason to postpone:
- Useful for production hardening, but not required for the first functional slice.

Priority: P1

### 5. Circuit breaking and fallback
- per-route circuit breaker
- timeout isolation
- degraded response templates
- fallback metrics

Reason to postpone:
- The first iteration should first prove the normal request path.

Priority: P2

### 6. Dynamic route management
- route definitions from Nacos or database
- hot reload for route changes
- route change audit trail

Reason to postpone:
- Static configuration is sufficient for the current learning demo.

Priority: P2

### 7. Gray release support
- version-based routing
- header-based canary rules
- small-traffic rollout strategy

Reason to postpone:
- Gray routing only becomes meaningful once multiple service versions exist.

Priority: P2

## Promotion rule

Move a gateway item into the main implementation only when:
- the baseline gateway route works end to end,
- the dependent service contracts are stable,
- and the added governance feature has a concrete traffic or operational reason.
