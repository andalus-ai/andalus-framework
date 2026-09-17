package tech.kayys.wayang.harness.api;

import tech.kayys.wayang.harness.lifecycle.HarnessExecutionStatus;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Encapsulates the final outcome, artifacts, and status of an agent execution within the Harness.
 */
public record HarnessResult(
        String executionId,
        HarnessExecutionStatus status,
        Object output,
        String error,
        Map<String, Object> metadata
) {

    public HarnessResult {
        Objects.requireNonNull(executionId, "executionId");
        Objects.requireNonNull(status, "status");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static HarnessResult success(String executionId, Object output) {
        return new HarnessResult(executionId, HarnessExecutionStatus.COMPLETED, output, null, Map.of());
    }

    public static HarnessResult success(String executionId, Object output, Map<String, Object> metadata) {
        return new HarnessResult(executionId, HarnessExecutionStatus.COMPLETED, output, null, metadata);
    }

    public static HarnessResult failure(String executionId, String error) {
        return new HarnessResult(executionId, HarnessExecutionStatus.FAILED, null, error, Map.of());
    }

    public static HarnessResult failure(String executionId, String error, Map<String, Object> metadata) {
        return new HarnessResult(executionId, HarnessExecutionStatus.FAILED, null, error, metadata);
    }

    public static HarnessResult cancelled(String executionId) {
        return new HarnessResult(executionId, HarnessExecutionStatus.CANCELLED, null, "Execution was cancelled", Map.of());
    }

    public boolean isSuccess() {
        return status == HarnessExecutionStatus.COMPLETED;
    }

    public Optional<Object> getOutput() {
        return Optional.ofNullable(output);
    }

    public Optional<String> getError() {
        return Optional.ofNullable(error);
    }
}
