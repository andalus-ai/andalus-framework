package tech.kayys.wayang.harness.workflow;

/**
 * Backoff timing configuration for iterative loops.
 */
public record LoopBackoff(
        long initialMillis,
        double multiplier,
        long maxMillis
) {

    public static LoopBackoff none() {
        return new LoopBackoff(0L, 1.0, 0L);
    }

    public static LoopBackoff fixed(long millis) {
        return new LoopBackoff(millis, 1.0, millis);
    }

    public static LoopBackoff exponential(long initialMillis, long maxMillis) {
        return new LoopBackoff(initialMillis, 2.0, maxMillis);
    }

    public long computeDelay(int iteration) {
        if (initialMillis <= 0) return 0;
        double delay = initialMillis * Math.pow(multiplier, Math.max(0, iteration - 1));
        return Math.min((long) delay, maxMillis);
    }
}
