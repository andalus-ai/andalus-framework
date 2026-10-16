package tech.kayys.andalus.a2a.durable;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Durable identity record that binds a local Claw execution to a remote A2A task (6.16).
 *
 * <p>This record captures the three-identity relationship described in 6.16.2:
 * <pre>
 *   Caller ExecutionId (exec-A)          ← stored as callerExecutionId String
 *       │
 *       └── A2A Operation
 *              ├── OperationId      (unique per invocation intent)
 *              ├── IdempotencyKey   (stable across retries — 6.16.13)
 *              ├── A2A TaskId       (task-B, assigned by remote agent)
 *              └── remoteEndpoint
 * </pre>
 *
 * <p><strong>NOT the execution identity.</strong> {@code A2ATaskId ≠ ExecutionId} (6.16.3).
 * The local Claw {@code ExecutionId} remains the canonical durable identity.
 * It is stored as a plain {@code String} here so this record has no compile-time
 * dependency on the harness execution layer (separation of concerns, SOLID).
 *
 * <p><strong>Idempotency (6.16.13):</strong> The {@code idempotencyKey} is stable across
 * retries. If a retry finds an existing operation with the same key, it reuses it rather
 * than creating a new remote task.
 */
public record A2AOperationRecord(
        /** Unique identifier for this A2A operation attempt. */
        String operationId,

        /**
         * The value of the local Claw ExecutionId that initiated this A2A call.
         * Stored as a String to keep andalus-a2a free of harness dependencies.
         */
        String callerExecutionId,

        /** Stable key used to de-duplicate requests across retries (6.16.13). */
        String idempotencyKey,

        /** The remote A2A task ID assigned by the remote agent (null until accepted). */
        String a2aTaskId,

        /** URI of the remote agent endpoint. */
        String remoteEndpoint,

        /** Current status of this A2A operation from the local perspective. */
        A2AOperationStatus status,

        /** Wall-clock time when this operation was created. */
        Instant createdAt,

        /** Wall-clock time when this operation reached a terminal status (null if active). */
        Instant completedAt,

        /** Number of submit retries attempted so far. */
        int retryCount,

        /** Checkpoint ID saved before the A2A call (crash recovery anchor — 6.16.20). */
        String checkpointId
) {
    public A2AOperationRecord {
        Objects.requireNonNull(operationId,        "operationId");
        Objects.requireNonNull(callerExecutionId,  "callerExecutionId");
        Objects.requireNonNull(idempotencyKey,     "idempotencyKey");
        Objects.requireNonNull(remoteEndpoint,     "remoteEndpoint");
        Objects.requireNonNull(status,             "status");
        if (createdAt == null) createdAt = Instant.now();
    }

    // ── Factories ─────────────────────────────────────────────────────────────

    /** Creates a new PENDING A2A operation with an auto-generated idempotency key. */
    public static A2AOperationRecord create(String callerExecutionId, String remoteEndpoint,
                                             String checkpointId) {
        Objects.requireNonNull(callerExecutionId, "callerExecutionId");
        String opId = "op-a2a-" + UUID.randomUUID();
        // Idempotency key is derived from callerExecutionId + operationId for stability
        String iKey = callerExecutionId + ":" + opId;
        return new A2AOperationRecord(opId, callerExecutionId, iKey, null,
                remoteEndpoint, A2AOperationStatus.PENDING, Instant.now(), null, 0, checkpointId);
    }

    /** Creates an operation with an explicit stable idempotency key (recovery/retry). */
    public static A2AOperationRecord createWithKey(String callerExecutionId, String remoteEndpoint,
                                                    String idempotencyKey, String checkpointId) {
        Objects.requireNonNull(callerExecutionId, "callerExecutionId");
        Objects.requireNonNull(idempotencyKey,    "idempotencyKey");
        String opId = "op-a2a-" + UUID.randomUUID();
        return new A2AOperationRecord(opId, callerExecutionId, idempotencyKey, null,
                remoteEndpoint, A2AOperationStatus.PENDING, Instant.now(), null, 0, checkpointId);
    }

    // ── Immutable transitions ──────────────────────────────────────────────────

    public A2AOperationRecord withA2aTaskId(String taskId) {
        return new A2AOperationRecord(operationId, callerExecutionId, idempotencyKey, taskId,
                remoteEndpoint, status, createdAt, completedAt, retryCount, checkpointId);
    }

    public A2AOperationRecord withStatus(A2AOperationStatus newStatus) {
        Instant completed = newStatus.isTerminal() ? Instant.now() : completedAt;
        return new A2AOperationRecord(operationId, callerExecutionId, idempotencyKey, a2aTaskId,
                remoteEndpoint, newStatus, createdAt, completed, retryCount, checkpointId);
    }

    public A2AOperationRecord withCheckpointId(String id) {
        return new A2AOperationRecord(operationId, callerExecutionId, idempotencyKey, a2aTaskId,
                remoteEndpoint, status, createdAt, completedAt, retryCount, id);
    }

    public A2AOperationRecord incrementRetry() {
        return new A2AOperationRecord(operationId, callerExecutionId, idempotencyKey, a2aTaskId,
                remoteEndpoint, A2AOperationStatus.PENDING, createdAt, null, retryCount + 1, checkpointId);
    }

    // ── Predicates ────────────────────────────────────────────────────────────

    public boolean isTerminal()  { return status.isTerminal(); }
    public boolean isPending()   { return status == A2AOperationStatus.PENDING; }
    public boolean isSubmitted() { return status == A2AOperationStatus.SUBMITTED; }
    public boolean isWaiting()   { return status == A2AOperationStatus.WAITING_FOR_REMOTE; }

    public Optional<String> taskId() { return Optional.ofNullable(a2aTaskId); }
}
