# JDBC fenced lease manager

Apply this schema before using JdbcFencedLeaseManager:

```sql
CREATE TABLE andalus_execution_lease (
    execution_id VARCHAR(512) NOT NULL PRIMARY KEY,
    generation BIGINT NOT NULL,
    holder_id VARCHAR(512) NULL,
    acquired_at_ms BIGINT NOT NULL,
    expires_at_ms BIGINT NOT NULL
);
```

The adapter requires transactional row locking with SELECT FOR UPDATE, primary-key
uniqueness and savepoints. It uses only JDBC APIs and adds no driver dependency.

Rows are permanent generation counters: never delete them on release or expiry.
Acquisition locks the row and increments generation, including reacquisition by
the same holder. Renewal retains generation; the fenced overload requires an
exact holder and token. Fenced release also requires an exact active holder and
generation. Storage errors fail closed; there is no local fallback.

Use a shared writable database for all nodes, not a read replica. Keep node
clocks synchronized or supply a shared clock policy. Configure the database and
pool so transactions honor SELECT FOR UPDATE. Dialects without the required
locking and savepoint behavior need a provider-specific implementation.
