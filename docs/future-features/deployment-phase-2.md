# Deployment Phase 2

This document collects deployment hardening work that should be implemented after the current Ubuntu hybrid deployment path is stable.

## Current iteration boundary

The current deployment baseline is:
- middleware in Docker Compose
- Java services on the Ubuntu host
- startup and verification steps documented in English and Chinese

Anything below should remain deferred until that baseline is proven end to end.

## Phase 2 backlog

### 1. Full Dockerized application deployment
- containerize each Java service
- image build conventions
- compose-based service startup order
- container log collection rules

Reason to postpone:
- The hybrid deployment is simpler for the first operational learning slice.

Priority: P1

### 2. systemd service management standardization
- one unit file per Java service
- restart policy tuning
- environment file conventions
- startup dependency ordering

Reason to postpone:
- The current docs can rely on direct process startup first.

Priority: P1

### 3. Reverse proxy and TLS
- Nginx entry layer
- HTTPS certificate handling
- upstream load balancing
- static request filtering

Reason to postpone:
- TLS and ingress concerns can be added after the services are already reachable and stable.

Priority: P1

### 4. CI/CD deployment automation
- package and publish artifacts
- remote deploy scripts
- rollback workflow
- deploy verification steps

Reason to postpone:
- Manual deployment helps learning in the current stage and keeps the first setup transparent.

Priority: P2

### 5. Production configuration separation
- env-specific config files
- secret management approach
- externalized service parameters
- release profile conventions

Reason to postpone:
- The first deployment guide can use simpler direct configuration for study purposes.

Priority: P2

### 6. Monitoring and alerting integration
- Prometheus metrics exposure
- Grafana dashboard templates
- log aggregation path
- host and JVM alert rules

Reason to postpone:
- Monitoring should follow after the deployment baseline is repeatable.

Priority: P2

### 7. Backup and recovery drills
- MySQL backup scripts
- MongoDB backup scripts
- Redis persistence verification
- restore rehearsal steps

Reason to postpone:
- Disaster recovery is important, but outside the first functional deployment slice.

Priority: P2

## Promotion rule

Move a deployment item into the main implementation only when:
- the hybrid baseline can be deployed repeatedly,
- the operational gain is clear,
- and the additional moving parts do not hide the core learning path.
