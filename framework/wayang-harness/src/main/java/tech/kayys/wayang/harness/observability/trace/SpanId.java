package tech.kayys.wayang.harness.observability.trace;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier for an individual execution span within a trace.
 */
public record SpanId(String value) {
    public SpanId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("SpanId cannot be blank");
        }
    }

    public static SpanId of(String value) {
        return new SpanId(value);
    }

    public static SpanId generate() {
        return new SpanId("span-" + UUID.randomUUID());
    }
}
