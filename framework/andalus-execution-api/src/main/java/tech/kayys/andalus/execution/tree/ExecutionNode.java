package tech.kayys.andalus.execution.tree;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable description of one execution in an agent execution tree.
 *
 * <p>An execution node represents lineage only. It does not define
 * workflow ordering or business-process semantics.</p>
 */
public record ExecutionNode(
        String executionId,
        String traceId,
        Optional<String> parentExecutionId,
        Instant createdAt
) {

    public ExecutionNode {
        Objects.requireNonNull(executionId, "executionId must not be null");
        if (executionId.isBlank()) {
            throw new IllegalArgumentException("executionId must not be blank");
        }

        Objects.requireNonNull(traceId, "traceId must not be null");
        if (traceId.isBlank()) {
            throw new IllegalArgumentException("traceId must not be blank");
        }

        parentExecutionId = parentExecutionId == null
                ? Optional.empty()
                : parentExecutionId;

        createdAt = createdAt == null
                ? Instant.now()
                : createdAt;
    }

    public boolean isRoot() {
        return parentExecutionId.isEmpty();
    }

    public static ExecutionNode root(
            String executionId,
            String traceId,
            Instant createdAt) {

        return new ExecutionNode(
                executionId,
                traceId,
                Optional.empty(),
                createdAt
        );
    }

    public static ExecutionNode child(
            String executionId,
            String traceId,
            String parentExecutionId,
            Instant createdAt) {

        Objects.requireNonNull(parentExecutionId, "parentExecutionId must not be null");

        return new ExecutionNode(
                executionId,
                traceId,
                Optional.of(parentExecutionId),
                createdAt
        );
    }
}
