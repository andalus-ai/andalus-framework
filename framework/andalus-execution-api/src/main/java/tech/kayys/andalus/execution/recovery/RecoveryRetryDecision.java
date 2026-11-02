package tech.kayys.andalus.execution.recovery;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/** Immutable result of evaluating one failed recovery attempt. */
public record RecoveryRetryDecision(
        Outcome outcome,
        int failedAttemptNumber,
        Optional<Instant> nextAttemptAt,
        String reason
) {
    public enum Outcome {
        RETRY,
        DEAD_LETTER,
        DO_NOT_RETRY,
        CANCELLED,
        DEADLINE_EXCEEDED
    }

    public RecoveryRetryDecision {
        Objects.requireNonNull(outcome, "outcome cannot be null");
        if (failedAttemptNumber < 1) {
            throw new IllegalArgumentException("failedAttemptNumber must be positive");
        }
        nextAttemptAt = nextAttemptAt == null ? Optional.empty() : nextAttemptAt;
        if ((outcome == Outcome.RETRY) != nextAttemptAt.isPresent()) {
            throw new IllegalArgumentException("Only RETRY decisions must contain nextAttemptAt");
        }
        reason = reason == null ? "" : reason;
    }
}
