# Andalus PostgreSQL Task Queue

This module provides `PostgresTaskQueue`, a JDBC adapter for the provider-neutral
`TaskQueue` contract in `andalus-harness`.

## Setup

Apply `src/main/resources/db/migration/V1__create_execution_task_queue.sql`
with your migration tool, or call `PostgresTaskQueue.ensureSchema()` for a
simple installation/test environment.

Construct the adapter with an application-managed `javax.sql.DataSource`.
The PostgreSQL JDBC driver is an optional runtime dependency of this module.

## Claim semantics

- Claims use `SELECT ... FOR UPDATE SKIP LOCKED` inside a database transaction.
- The database clock is authoritative for visibility deadlines.
- Every successful claim increments `claim_generation`.
- Acknowledgement, negative acknowledgement, and visibility extension require
  the exact claimant, generation, and an unexpired claim.
- Expired claims become eligible for redelivery; delivery is at-least-once.

External side effects are not part of the queue transaction. Activities must
use idempotency keys, an outbox/inbox, deduplication, or compensation.

## Operations

The queue table should be monitored for old unacknowledged rows and repeated
claim expiry. Retention/purge policy is intentionally left to deployment
operations so queue records are not deleted before audit/reconciliation needs
are met.

The adapter is PostgreSQL-specific. Other brokers/databases should implement
the same `TaskQueue` contract in separate modules.
