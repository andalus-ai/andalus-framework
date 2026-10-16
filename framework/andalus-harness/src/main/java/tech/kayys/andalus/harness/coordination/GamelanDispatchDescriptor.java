package tech.kayys.andalus.harness.coordination;

import java.util.Objects;

/**
 * Minimal, framework-local view of an active Gamelan node dispatch (6.17.3.7).
 *
 * <p>The Gamelan-side adapter translates its own types
 * ({@code WorkflowRunId}, {@code NodeId}, {@code NodeDispatchReservation}) into this
 * descriptor, so the Andalus harness never depends on Gamelan's type system.</p>
 */
public record GamelanDispatchDescriptor(
        String tenantId,
        String workflowRunId,
        String nodeId,
        int attempt,
        String reservationId
) {
    public GamelanDispatchDescriptor {
        Objects.requireNonNull(tenantId, "tenantId");
        Objects.requireNonNull(workflowRunId, "workflowRunId");
        Objects.requireNonNull(nodeId, "nodeId");
        if (attempt < 1) {
            throw new IllegalArgumentException("attempt must be >= 1");
        }
    }

    /** Same derived identity as {@link AgentExecutionBinding#dispatchId()}. */
    public String dispatchId() {
        return tenantId + ":" + workflowRunId + ":" + nodeId + ":" + attempt;
    }
}
