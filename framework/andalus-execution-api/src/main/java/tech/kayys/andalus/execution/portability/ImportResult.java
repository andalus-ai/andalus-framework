package tech.kayys.andalus.execution.portability;

import tech.kayys.andalus.execution.mobility.ExecutionIdentity;
import java.util.Objects;

/**
 * Result of importing and hydrating a portable execution on a target runtime.
 */
public record ImportResult(
    boolean success,
    ExecutionIdentity executionId,
    String reason
) {
    public ImportResult {
        Objects.requireNonNull(executionId, "executionId cannot be null");
    }

    public static ImportResult success(ExecutionIdentity executionId) {
        return new ImportResult(true, executionId, "Successfully imported");
    }

    public static ImportResult failure(ExecutionIdentity executionId, String reason) {
        return new ImportResult(false, executionId, reason);
    }
}
