package tech.kayys.andalus.harness.decision.trace;

import java.util.Map;
import java.util.Optional;

/**
 * Request to start a new decision trace.
 */
public record DecisionTraceRequest(
        String traceId,
        String executionId,
        String turnId,
        String strategyId,
        Optional<String> goalId,
        Optional<String> taskId,
        Optional<String> contextSnapshotReference,
        Optional<String> stateRevision,
        Map<String, Object> metadata
) {
    public DecisionTraceRequest {
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId is required");
        }
        traceId = traceId == null ? "" : traceId;
        turnId = turnId == null ? "" : turnId;
        strategyId = strategyId == null ? "" : strategyId;
        goalId = goalId == null ? Optional.empty() : goalId;
        taskId = taskId == null ? Optional.empty() : taskId;
        contextSnapshotReference = contextSnapshotReference == null
                ? Optional.empty() : contextSnapshotReference;
        stateRevision = stateRevision == null
                ? Optional.empty() : stateRevision;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static DecisionTraceRequest of(String executionId, String turnId, String strategyId) {
        return new DecisionTraceRequest(
                "",
                executionId,
                turnId,
                strategyId,
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                Map.of()
        );
    }
}
