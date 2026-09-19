package tech.kayys.wayang.spi.execution;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public record ExecutionInfo(
        String executionId,
        String tenantId,
        String userId,
        String agentId,
        String workflowId,
        ExecutionState state,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt,
        Instant updatedAt,
        String correlationId,
        String failureCode,
        String failureMessage,
        Map<String, Object> attributes) {

    public ExecutionInfo {
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException(
                    "executionId must not be blank");
        }

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public Optional<Instant> started() {
        return Optional.ofNullable(startedAt);
    }

    public Optional<Instant> completed() {
        return Optional.ofNullable(completedAt);
    }

    public Optional<String> failureCodeOptional() {
        return Optional.ofNullable(failureCode);
    }

    public Optional<String> failureMessageOptional() {
        return Optional.ofNullable(failureMessage);
    }
}
