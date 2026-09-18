package tech.kayys.wayang.harness.workflow;

import java.util.Objects;

/**
 * Bounded execution safety policy governing loop cycles.
 */
public record LoopPolicy(
        int maxIterations,
        long maxDurationMillis,
        LoopBackoff backoff
) {

    public LoopPolicy {
        if (maxIterations <= 0) {
            throw new IllegalArgumentException("maxIterations must be greater than 0");
        }
        backoff = backoff != null ? backoff : LoopBackoff.none();
    }

    public static LoopPolicy defaults() {
        return new LoopPolicy(10, 120000L, LoopBackoff.none());
    }

    public static LoopPolicy maxIterations(int max) {
        return new LoopPolicy(max, 120000L, LoopBackoff.none());
    }

    public static LoopPolicy withBackoff(int max, LoopBackoff backoff) {
        return new LoopPolicy(max, 120000L, backoff);
    }
}
