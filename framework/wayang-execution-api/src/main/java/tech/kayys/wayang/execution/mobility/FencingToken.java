package tech.kayys.wayang.execution.mobility;

import java.util.Objects;

/**
 * Monotonically increasing fencing token to prevent split-brain writes.
 */
public record FencingToken(long generation) implements Comparable<FencingToken> {
    public FencingToken {
        if (generation < 0) {
            throw new IllegalArgumentException("generation must be non-negative");
        }
    }

    public static FencingToken of(long generation) {
        return new FencingToken(generation);
    }

    public static FencingToken initial() {
        return new FencingToken(1L);
    }

    public FencingToken next() {
        return new FencingToken(generation + 1);
    }

    public boolean isAfter(FencingToken other) {
        Objects.requireNonNull(other, "other cannot be null");
        return this.generation > other.generation;
    }

    @Override
    public int compareTo(FencingToken o) {
        return Long.compare(this.generation, o.generation);
    }
}
