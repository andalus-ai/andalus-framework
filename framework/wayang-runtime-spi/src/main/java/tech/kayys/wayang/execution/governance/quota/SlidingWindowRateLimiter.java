package tech.kayys.wayang.execution.governance.quota;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Thread-safe sliding window rate limiter implementation.
 */
public final class SlidingWindowRateLimiter implements RateLimiter {

    private final int maxPermits;
    private final Duration windowDuration;
    private final Map<String, ConcurrentLinkedQueue<Instant>> windowMap = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(int maxPermits, Duration windowDuration) {
        if (maxPermits <= 0) {
            throw new IllegalArgumentException("maxPermits must be positive");
        }
        this.maxPermits = maxPermits;
        this.windowDuration = Objects.requireNonNull(windowDuration, "windowDuration cannot be null");
    }

    @Override
    public synchronized boolean tryAcquire(String key) {
        String partition = (key == null || key.isBlank()) ? "default" : key.trim();
        Instant now = Instant.now();
        Instant cutoff = now.minus(windowDuration);

        ConcurrentLinkedQueue<Instant> timestamps = windowMap.computeIfAbsent(partition, k -> new ConcurrentLinkedQueue<>());

        // Evict expired entries
        while (!timestamps.isEmpty() && timestamps.peek().isBefore(cutoff)) {
            timestamps.poll();
        }

        if (timestamps.size() < maxPermits) {
            timestamps.add(now);
            return true;
        }

        return false;
    }

    @Override
    public void reset() {
        windowMap.clear();
    }
}
