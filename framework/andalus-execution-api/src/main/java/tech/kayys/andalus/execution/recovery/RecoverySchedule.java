package tech.kayys.andalus.execution.recovery;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Durable scheduling intent. idempotencyKey must remain stable across redelivery and retries
 * of the enqueue operation; adapters must not create duplicate scheduled work for that key.
 */
public record RecoverySchedule(
        String idempotencyKey,
        String executionId,
        int failedAttemptNumber,
        RecoveryFailureCategory failureCategory,
        Instant notBefore,
        Optional<Instant> deadline
) {
    public RecoverySchedule {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new IllegalArgumentException("idempotencyKey cannot be blank");
        }
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId cannot be blank");
        }
        if (failedAttemptNumber < 1) {
            throw new IllegalArgumentException("failedAttemptNumber must be positive");
        }
        Objects.requireNonNull(failureCategory, "failureCategory cannot be null");
        Objects.requireNonNull(notBefore, "notBefore cannot be null");
        deadline = deadline == null ? Optional.empty() : deadline;
        if (deadline.filter(value -> !notBefore.isBefore(value)).isPresent()) {
            throw new IllegalArgumentException("notBefore must be before deadline");
        }
    }
}
