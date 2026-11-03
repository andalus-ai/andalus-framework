# Andalus PostgreSQL Task Queue

This module provides `PostgresTaskQueue`, a JDBC adapter for the provider-neutral
`TaskQueue` contract in `andalus-harness`.

## Setup

Apply migrations in order:

1. `V1__create_execution_task_queue.sql`
2. `V2__add_task_retry_dead_letter.sql`

`PostgresTaskQueue.ensureSchema()` is also available for simple installation and
integration-test environments. Production deployments should use their migration
tool and an application-managed `javax.sql.DataSource`.

The PostgreSQL JDBC driver is an optional runtime dependency of this module.

## Claim semantics

- Claims use `SELECT ... FOR UPDATE SKIP LOCKED` inside a database transaction.
- The database clock is authoritative for visibility deadlines.
- Every successful claim increments `claim_generation`.
- Acknowledgement, negative acknowledgement, visibility extension, and failure
  disposition require the exact claimant, generation, and an unexpired claim.
- Expired claims become eligible for redelivery; delivery is at-least-once.

## Retry and dead-letter semantics

- `TaskFailurePolicy` classifies each handler exception as retryable with a delay
  or permanently dead-letterable.
- The queue increments a durable delivery failure count.
- `AssignmentConstraints.maxRetries` is the number of retries allowed after the
  initial delivery. The next retryable failure is moved to the dead-letter set.
- Failure count, category, message, retry scheduling, claim release, and
  dead-letter timestamp are updated atomically under the current claim fence.
- `listDeadLetters(limit)` returns an immutable, oldest-first snapshot for
  operator inspection. Re-drive/replay is intentionally an explicit operator
  workflow and is not automatic.

## Delivery guarantees and operations

External side effects are not part of the queue transaction. Activities must
use idempotency keys, an outbox/inbox, deduplication, or compensation. A stale
worker cannot acknowledge, retry, or dead-letter a task after its claim has
expired or been replaced.

Monitor old unacknowledged rows, repeated claim expiry, retry counts, and
dead-letter volume. Define retention and audited re-drive procedures before
production use so records are not deleted before audit/reconciliation needs
are met.

The adapter is PostgreSQL-specific. Other brokers/databases should implement
the same `TaskQueue` contract in separate modules.
