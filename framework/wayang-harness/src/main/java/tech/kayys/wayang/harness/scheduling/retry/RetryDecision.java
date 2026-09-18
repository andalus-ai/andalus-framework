package tech.kayys.wayang.harness.scheduling.retry;

import java.time.Duration;

/**
 * Decision evaluating whether and when to retry an operation.
 */
public record RetryDecision(
        boolean shouldRetry,
        Duration delay,
        String reason
) {
    public static RetryDecision retryAfter(Duration delay, String reason) {
        return new RetryDecision(true, delay, reason);
    }

    public static RetryDecision noRetry(String reason) {
        return new RetryDecision(false, Duration.ZERO, reason);
    }
}
