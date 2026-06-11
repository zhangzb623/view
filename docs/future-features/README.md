# Future Features Index

This directory collects features that are intentionally postponed to a later iteration.

## Usage rule

When a feature is valuable but would expand the current iteration too much, document it here first instead of mixing it into the current implementation plan.

## Documents

- [Chat Server Phase 2](chat-server-phase-2.md) — websocket UI, Redis session state, offline messages, history, file transfer, and multi-instance sync
- [Gateway Phase 2](gateway-phase-2.md) — advanced routing, throttling, and observability improvements
- [Seata Phase 2](seata-phase-2.md) — full distributed transaction rollout and expansion
- [Deployment Phase 2](deployment-phase-2.md) — production-hardening and all-Docker deployment evolution

## Rules for adding a future feature

1. Keep the current iteration focused on one working slice.
2. Put postponed items in a dedicated future-features document.
3. Record the dependency that must exist before the feature can be promoted.
4. Assign a priority so future work stays ordered.
