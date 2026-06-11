# Chat Server Phase 2

This document collects Chat Server capabilities that should be implemented after the Netty first iteration is stable.

## Current iteration boundary

The first iteration should already provide:
- Netty server startup
- login / logout
- heartbeat
- private message routing
- group message broadcast
- basic logs

Anything below should stay in this phase-2 document until the first iteration is complete.

## Phase 2 backlog

### 1. WebSocket frontend
- Browser chat page
- Login panel
- Conversation list
- Message pane
- Connection status indicator

Reason to postpone:
- The first iteration is protocol- and server-focused, not UI-focused.

Priority: P1

### 2. Redis session state
- Persist online user mappings in Redis
- Cross-instance session lookup
- Heartbeat expiry cleanup

Reason to postpone:
- Single-node Netty does not need Redis for the first iteration.

Priority: P1

### 3. Offline messages
- Store messages for offline users
- Deliver queued messages on login
- Retry policy for delayed delivery

Reason to postpone:
- Offline delivery adds storage and retry semantics beyond the first learning slice.

Priority: P1

### 4. Message history
- Query conversation history by user or group
- Pagination
- Time-range filtering

Reason to postpone:
- History querying should wait until the core live chat path is stable.

Priority: P2

### 5. File and image transfer
- Upload file metadata
- Transfer file payloads
- Preview support for images

Reason to postpone:
- File transfer significantly expands protocol and storage concerns.

Priority: P2

### 6. Read receipts
- Per-message read acknowledgement
- Group read summary
- Unread counters

Reason to postpone:
- Read tracking is useful, but not required for the first server slice.

Priority: P2

### 7. Group management backend
- Create group
- Add / remove members
- Group owner and admin roles

Reason to postpone:
- Group membership management is a separate workflow from routing.

Priority: P2

### 8. Multi-instance message synchronization
- Broadcast between Chat Server instances
- Shared state coordination
- Failure recovery for instance failover

Reason to postpone:
- The first iteration is single-node by design.

Priority: P0 for real scale, P2 for learning demo

### 9. MongoDB chat persistence
- Persist chat messages
- Persist user presence events
- Persist delivery logs

Reason to postpone:
- The first iteration can run with basic logs only.

Priority: P2

## Promotion rule

Move an item into the main Chat Server implementation only when:
- the first iteration is stable,
- the feature has a clear user story,
- and the dependency chain is already present.
