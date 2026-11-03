package tech.kayys.andalus.harness.fabric;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * A bounded, structured observation emitted by an execution lease handler.
 * Failure details intentionally contain only an exception type, never a message.
 */
public record ExecutionLeaseObservation(
        Type type,
        String executionId,
        long fencingGeneration,
        Instant occurredAt,
        Duration renewalDuration,
        String failureType) {

    public enum Type {
        ACQUIRED,
        RENEWAL_SUCCEEDED,
        RENEWAL_REJECTED,
        RENEWAL_FAILED,
        OWNERSHIP_LOST,
        RELEASED
    }

    public ExecutionLeaseObservation {
        Objects.requireNonNull(type, "type cannot be null");
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId cannot be blank");
        }
        if (fencingGeneration < 0) {
            throw new IllegalArgumentException("fencingGeneration cannot be negative");
        }
        occurredAt = occurredAt == null ? Instant.now() : occurredAt;
        if (renewalDuration != null && renewalDuration.isNegative()) {
            throw new IllegalArgumentException("renewalDuration cannot be negative");
        }
        if (failureType != null && failureType.isBlank()) {
            throw new IllegalArgumentException("failureType cannot be blank");
        }
    }
}
