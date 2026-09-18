package tech.kayys.wayang.harness.scheduling.retry;

import java.time.Duration;

/**
 * Strategy for computing backoff duration between retry attempts.
 */
@FunctionalInterface
public interface BackoffStrategy {

    Duration nextDelay(int attempt);

    static BackoffStrategy fixed(Duration delay) {
        return attempt -> delay;
    }

    static BackoffStrategy exponential(Duration initial, double factor, Duration maxDelay) {
        return attempt -> {
            long millis = (long) (initial.toMillis() * Math.pow(factor, Math.max(0, attempt)));
            return Duration.ofMillis(Math.min(millis, maxDelay.toMillis()));
        };
    }
}
