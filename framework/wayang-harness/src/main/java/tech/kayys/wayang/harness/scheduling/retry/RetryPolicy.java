package tech.kayys.wayang.harness.scheduling.retry;

import java.time.Duration;

/**
 * Universal policy for determining retry behavior across model, tool, and execution operations.
 */
public interface RetryPolicy {

    RetryDecision evaluate(Throwable failure, int attempt);

    static RetryPolicy maxAttempts(int maxAttempts, BackoffStrategy backoff) {
        return (failure, attempt) -> {
            if (attempt >= maxAttempts) {
                return RetryDecision.noRetry("Exceeded max attempts: " + maxAttempts);
            }
            Duration delay = backoff != null ? backoff.nextDelay(attempt) : Duration.ZERO;
            return RetryDecision.retryAfter(delay, "Retry attempt " + (attempt + 1));
        };
    }

    static RetryPolicy none() {
        return (failure, attempt) -> RetryDecision.noRetry("No retry configured");
    }
}
