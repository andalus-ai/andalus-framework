package tech.kayys.andalus.harness.coordination;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Durable correlation record binding a Gamelan workflow node dispatch to an Andalus
 * Claw execution (6.17.1–6.17.3).
 *
 * <p>Ownership boundaries:</p>
 * <pre>
 *   Gamelan owns:  TenantId → WorkflowRunId → NodeId → node attempt
 *   Andalus owns:  ExecutionId → AttemptId → Journal → Checkpoint → State
 * </pre>
 *
 * <p><strong>Key rule (6.17.3):</strong> {@code WorkflowRunId ≠ ExecutionId} and
 * {@code NodeId ≠ ExecutionId}.</p>
 *
 * <p><strong>Dispatch identity (6.17.3.31):</strong> the Gamelan source does not expose
 * an explicit dispatch UUID; the durable logical dispatch identity is therefore derived
 * from the Gamelan-owned identity:
 * {@code tenantId + workflowRunId + nodeId + attempt}. If Gamelan later introduces a
 * real dispatch id it can be substituted here without changing the Claw model.</p>
 */
public record AgentExecutionBinding(
        /** Tenant scope — cross-tenant binding is never allowed (6.17.2.19). */
        String tenantId,

        /** Gamelan workflow run identifier. */
        String workflowRunId,

        /** Gamelan node identifier within the workflow run. */
        String nodeId,

        /** Gamelan node attempt for this dispatch (1-based). */
        int attempt,

        /** The Andalus Claw execution assigned to this node dispatch. */
        ExecutionId executionId,

        /** Optional Gamelan dispatch reservation id (idempotency evidence). */
        String reservationId,

        /** When the binding was established. */
        Instant createdAt,

        /** When the bound Claw execution reached a terminal state (null while active). */
        Instant completedAt
) {
    public AgentExecutionBinding {
        Objects.requireNonNull(tenantId,      "tenantId");
        Objects.requireNonNull(workflowRunId, "workflowRunId");
        Objects.requireNonNull(nodeId,        "nodeId");
        Objects.requireNonNull(executionId,   "executionId");
        if (attempt < 1) {
            throw new IllegalArgumentException("attempt must be >= 1");
        }
        createdAt = createdAt != null ? createdAt : Instant.now();
    }

    /**
     * Derived durable dispatch identity — the logical Gamelan dispatch this binding
     * belongs to. Uniqueness is enforced on this value (6.17.2.16).
     */
    public String dispatchId() {
        return tenantId + ":" + workflowRunId + ":" + nodeId + ":" + attempt;
    }

    /** @return true while the bound execution has not reached a terminal state. */
    public boolean isActive() {
        return completedAt == null;
    }

    /** Marks the binding completed (record retained — 6.17.2.23). */
    public AgentExecutionBinding withCompleted() {
        return completedAt != null ? this : new AgentExecutionBinding(
                tenantId, workflowRunId, nodeId, attempt, executionId, reservationId, createdAt, Instant.now());
    }

    public Optional<String> reservation() {
        return Optional.ofNullable(reservationId);
    }

    /** Factory for the standard case (with reservation), first attempt. */
    public static AgentExecutionBinding of(String tenantId, String workflowRunId,
                                            String nodeId, ExecutionId executionId,
                                            String reservationId) {
        return new AgentExecutionBinding(tenantId, workflowRunId, nodeId, 1, executionId,
                reservationId, Instant.now(), null);
    }

    /** Factory without reservation id (test/compatibility), first attempt. */
    public static AgentExecutionBinding of(String tenantId, String workflowRunId,
                                            String nodeId, ExecutionId executionId) {
        return new AgentExecutionBinding(tenantId, workflowRunId, nodeId, 1, executionId,
                null, Instant.now(), null);
    }

    /** Factory for an explicit Gamelan node attempt. */
    public static AgentExecutionBinding of(String tenantId, String workflowRunId, String nodeId,
                                            int attempt, ExecutionId executionId, String reservationId) {
        return new AgentExecutionBinding(tenantId, workflowRunId, nodeId, attempt, executionId,
                reservationId, Instant.now(), null);
    }
}
