package tech.kayys.wayang.harness.observability.trace;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier for an execution trace tree.
 */
public record TraceId(String value) {
    public TraceId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("TraceId cannot be blank");
        }
    }

    public static TraceId of(String value) {
        return new TraceId(value);
    }

    public static TraceId generate() {
        return new TraceId("trace-" + UUID.randomUUID());
    }
}
