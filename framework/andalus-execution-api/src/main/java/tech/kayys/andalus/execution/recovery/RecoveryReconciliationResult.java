package tech.kayys.andalus.execution.recovery;

import tech.kayys.andalus.execution.attempt.AttemptId;

import java.util.Objects;
import java.util.Optional;

/** Immutable result of one recovery reconciliation pass. */
public record RecoveryReconciliationResult(
        String executionId,
        RecoveryReconciliationAction action,
        Optional<AttemptId> attemptId,
        String detail
) {
    public RecoveryReconciliationResult {
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId must not be blank");
        }
        Objects.requireNonNull(action, "action cannot be null");
        attemptId = attemptId == null ? Optional.empty() : attemptId;
        if (detail == null || detail.isBlank()) {
            throw new IllegalArgumentException("detail must not be blank");
        }
    }
}
