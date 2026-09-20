package tech.kayys.wayang.execution.mobility;

import java.util.Map;
import java.util.Objects;

/**
 * Opaque execution cursor representing where execution is within a workflow.
 */
public record ExecutionCursor(
        String workflowId,
        String nodeId,
        int attempt,
        String phase,
        Map<String, Object> offset
) {
    public ExecutionCursor {
        Objects.requireNonNull(workflowId, "workflowId cannot be null");
        Objects.requireNonNull(nodeId, "nodeId cannot be null");
        phase = phase == null ? "EXECUTION" : phase;
        offset = offset == null ? Map.of() : Map.copyOf(offset);
    }

    public static ExecutionCursor of(String workflowId, String nodeId, int attempt, String phase) {
        return new ExecutionCursor(workflowId, nodeId, attempt, phase, Map.of());
    }

    public static ExecutionCursor initial(String workflowId) {
        return new ExecutionCursor(workflowId, "START", 1, "INITIALIZING", Map.of());
    }
}
