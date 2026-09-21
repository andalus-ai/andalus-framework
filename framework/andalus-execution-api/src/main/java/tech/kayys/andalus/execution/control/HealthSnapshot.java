package tech.kayys.andalus.execution.control;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Health assessment snapshot of an active execution.
 */
public record HealthSnapshot(
        String executionId,
        HealthStatus status,
        Map<String, Object> details,
        Instant checkedAt
) {
    public HealthSnapshot {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(status, "status cannot be null");
        details = details == null ? Map.of() : Map.copyOf(details);
        if (checkedAt == null) {
            checkedAt = Instant.now();
        }
    }

    public static HealthSnapshot healthy(String executionId) {
        return new HealthSnapshot(executionId, HealthStatus.HEALTHY, Map.of(), Instant.now());
    }

    public static HealthSnapshot of(String executionId, HealthStatus status, Map<String, Object> details) {
        return new HealthSnapshot(executionId, status, details, Instant.now());
    }
}
