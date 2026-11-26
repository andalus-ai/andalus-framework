package tech.kayys.andalus.execution.recovery;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Bounded exponential-backoff policy for recovery attempts.
 *
 * maxAttempts counts the initial attempt plus retries. Jitter is deterministic when the caller
 * supplies a stable sample, allowing adapters and tests to calculate the same due time.
 */
public record RecoveryRetryPolicy(
        int maxAttempts,
        Duration initialDelay,
        Duration maxDelay,
        double multiplier,
        double jitterRatio
) {
    public RecoveryRetryPolicy {
        if (maxAttempts < 1) {
            throw new IllegalArgumentException("maxAttempts must be positive");
        }
        Objects.requireNonNull(initialDelay, "initialDelay cannot be null");
        Objects.requireNonNull(maxDelay, "maxDelay cannot be null");
        if (initialDelay.isNegative() || initialDelay.isZero()) {
            throw new IllegalArgumentException("initialDelay must be positive");
        }
        if (maxDelay.isNegative() || maxDelay.isZero() || maxDelay.compareTo(initialDelay) < 0) {
            throw new IllegalArgumentException("maxDelay must be >= initialDelay and positive");
        }
        if (!Double.isFinite(multiplier) || multiplier < 1.0d) {
            throw new IllegalArgumentException("multiplier must be finite and >= 1");
        }
        if (!Double.isFinite(jitterRatio) || jitterRatio < 0.0d || jitterRatio > 1.0d) {
            throw new IllegalArgumentException("jitterRatio must be between 0 and 1");
        }
    }

    /**
     * Evaluates a failed attempt. jitterSample must be in [0, 1] and maps linearly to
     * [-jitterRatio, +jitterRatio]. The caller owns randomness and should persist the resulting
     * nextAttemptAt rather than recalculate it after enqueue.
     */
    public RecoveryRetryDecision evaluate(
            RecoveryFailureCategory category,
            int failedAttemptNumber,
            Instant now,
            Optional<Instant> deadline,
            boolean cancelled,
            double jitterSample
    ) {
        Objects.requireNonNull(category, "category cannot be null");
        Objects.requireNonNull(now, "now cannot be null");
        deadline = deadline == null ? Optional.empty() : deadline;
        if (failedAttemptNumber < 1) {
            throw new IllegalArgumentException("failedAttemptNumber must be positive");
        }
        if (!Double.isFinite(jitterSample) || jitterSample < 0.0d || jitterSample > 1.0d) {
            throw new IllegalArgumentException("jitterSample must be between 0 and 1");
        }
        if (cancelled || category == RecoveryFailureCategory.CANCELLED) {
            return decision(RecoveryRetryDecision.Outcome.CANCELLED, failedAttemptNumber,
                    "Execution is cancelled");
        }
        if (category == RecoveryFailureCategory.DEADLINE_EXCEEDED
                || deadline.filter(value -> !now.isBefore(value)).isPresent()) {
            return decision(RecoveryRetryDecision.Outcome.DEADLINE_EXCEEDED, failedAttemptNumber,
                    "Execution deadline has elapsed");
        }
        if (category != RecoveryFailureCategory.TRANSIENT) {
            return decision(RecoveryRetryDecision.Outcome.DO_NOT_RETRY, failedAttemptNumber,
                    category == RecoveryFailureCategory.OPERATOR_ACTION_REQUIRED
                            ? "Operator action is required" : "Failure is not retryable");
        }
        if (failedAttemptNumber >= maxAttempts) {
            return decision(RecoveryRetryDecision.Outcome.DEAD_LETTER, failedAttemptNumber,
                    "Maximum recovery attempts exhausted");
        }

        double baseMillis = initialDelay.toMillis() * Math.pow(multiplier, failedAttemptNumber - 1);
        double cappedMillis = Math.min(baseMillis, maxDelay.toMillis());
        double jitterFactor = 1.0d - jitterRatio + (2.0d * jitterRatio * jitterSample);
        long delayMillis = Math.max(1L, (long) Math.min(maxDelay.toMillis(), cappedMillis * jitterFactor));
        Instant nextAttemptAt;
        try {
            nextAttemptAt = now.plusMillis(delayMillis);
        } catch (RuntimeException overflow) {
            return decision(RecoveryRetryDecision.Outcome.DEAD_LETTER, failedAttemptNumber,
                    "Calculated retry time is outside the supported time range");
        }
        if (deadline.filter(value -> !nextAttemptAt.isBefore(value)).isPresent()) {
            return decision(RecoveryRetryDecision.Outcome.DEADLINE_EXCEEDED, failedAttemptNumber,
                    "Next retry would exceed the execution deadline");
        }
        return new RecoveryRetryDecision(RecoveryRetryDecision.Outcome.RETRY, failedAttemptNumber,
                Optional.of(nextAttemptAt), "Transient failure; retry scheduled");
    }

    private static RecoveryRetryDecision decision(
            RecoveryRetryDecision.Outcome outcome, int attempt, String reason) {
        return new RecoveryRetryDecision(outcome, attempt, Optional.empty(), reason);
    }
}
