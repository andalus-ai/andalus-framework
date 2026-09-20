package tech.kayys.wayang.execution.control;

import java.time.Instant;
import java.util.Objects;

/**
 * Result returned by ExecutionController after applying a control command.
 */
public record ControlResult(
        String executionId,
        boolean success,
        ControlAction action,
        String message,
        Instant completedAt
) {
    public ControlResult {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(action, "action cannot be null");
        if (completedAt == null) {
            completedAt = Instant.now();
        }
    }

    public static ControlResult success(String executionId, ControlAction action, String message) {
        return new ControlResult(executionId, true, action, message, Instant.now());
    }

    public static ControlResult failure(String executionId, ControlAction action, String error) {
        return new ControlResult(executionId, false, action, error, Instant.now());
    }
}
