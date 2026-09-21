package tech.kayys.andalus.harness.scheduling.retry;

import tech.kayys.andalus.execution.attempt.ExecutionAttempt;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

/**
 * Result of evaluating a durable retry decision.
 */
public record RetryResult(
        RetryOutcome outcome,
        Optional<ExecutionAttempt> nextAttempt,
        Optional<Instant> scheduledAt,
        String reason
) {
    public enum RetryOutcome {
        SCHEDULED,
        TERMINATED,
        TIMED_OUT
    }

    public RetryResult {
        Objects.requireNonNull(outcome, "outcome");
        Objects.requireNonNull(nextAttempt, "nextAttempt");
        Objects.requireNonNull(scheduledAt, "scheduledAt");
        Objects.requireNonNull(reason, "reason");
    }

    public static RetryResult scheduled(ExecutionAttempt nextAttempt, Instant scheduledAt, String reason) {
        return new RetryResult(RetryOutcome.SCHEDULED, Optional.of(nextAttempt), Optional.of(scheduledAt), reason);
    }

    public static RetryResult terminated(String reason) {
        return new RetryResult(RetryOutcome.TERMINATED, Optional.empty(), Optional.empty(), reason);
    }

    public static RetryResult timedOut(String reason) {
        return new RetryResult(RetryOutcome.TIMED_OUT, Optional.empty(), Optional.empty(), reason);
    }

    public boolean isScheduled() {
        return outcome == RetryOutcome.SCHEDULED;
    }

    public boolean isTimedOut() {
        return outcome == RetryOutcome.TIMED_OUT;
    }

    public boolean isTerminated() {
        return outcome == RetryOutcome.TERMINATED;
    }
}
