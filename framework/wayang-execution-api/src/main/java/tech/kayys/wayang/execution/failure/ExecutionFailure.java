package tech.kayys.wayang.execution.failure;

import java.util.Objects;
import java.util.Optional;

public record ExecutionFailure(
        FailureId id,
        String executionId,
        FailureCategory category,
        FailureSeverity severity,
        boolean recoverable,
        String message,
        Optional<String> stackSummary,
        FailureContext context
) {
    public ExecutionFailure {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(category, "category cannot be null");
        Objects.requireNonNull(severity, "severity cannot be null");
        Objects.requireNonNull(message, "message cannot be null");
    }
}
