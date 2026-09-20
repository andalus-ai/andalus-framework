package tech.kayys.wayang.execution.provenance;

import java.util.Objects;
import java.util.UUID;

/**
 * Distributed trace identifier tying causal chains together.
 */
public record TraceId(String value) {
    public TraceId {
        Objects.requireNonNull(value, "value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank");
        }
    }

    public static TraceId of(String value) {
        return new TraceId(value);
    }

    public static TraceId generate() {
        return new TraceId("trace-" + UUID.randomUUID());
    }
}
