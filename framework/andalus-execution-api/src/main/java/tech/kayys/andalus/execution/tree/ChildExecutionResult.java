package tech.kayys.andalus.execution.tree;

import tech.kayys.andalus.execution.AgentResult;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Result of a delegated child execution.
 */
public record ChildExecutionResult(
        String executionId,
        String parentExecutionId,
        String traceId,
        AgentResult result,
        Instant completedAt,
        Map<String, Object> metadata
) {

    public ChildExecutionResult {
        Objects.requireNonNull(executionId, "executionId must not be null");
        Objects.requireNonNull(
                parentExecutionId,
                "parentExecutionId must not be null");
        Objects.requireNonNull(traceId, "traceId must not be null");
        Objects.requireNonNull(result, "result must not be null");

        completedAt = completedAt == null
                ? Instant.now()
                : completedAt;

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean successful() {
        return result.response().success();
    }
}
