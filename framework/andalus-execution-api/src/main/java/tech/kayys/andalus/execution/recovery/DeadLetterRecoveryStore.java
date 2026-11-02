package tech.kayys.andalus.execution.recovery;

import java.time.Instant;
import java.util.List;

/**
 * Durable quarantine for recovery work that cannot be retried automatically.
 * Implementations should make record() idempotent by executionId and failedAttemptNumber.
 */
public interface DeadLetterRecoveryStore {
    void record(DeadLetterRecoveryEntry entry);

    List<DeadLetterRecoveryEntry> list(String executionId);

    record DeadLetterRecoveryEntry(
            String executionId,
            int failedAttemptNumber,
            RecoveryFailureCategory failureCategory,
            String reason,
            Instant recordedAt
    ) {
        public DeadLetterRecoveryEntry {
            if (executionId == null || executionId.isBlank()) {
                throw new IllegalArgumentException("executionId cannot be blank");
            }
            if (failedAttemptNumber < 1) {
                throw new IllegalArgumentException("failedAttemptNumber must be positive");
            }
            if (failureCategory == null || recordedAt == null) {
                throw new IllegalArgumentException("failureCategory and recordedAt are required");
            }
            reason = reason == null ? "" : reason;
        }
    }
}
