package tech.kayys.wayang.harness.controlplane;

import java.util.Objects;
import java.util.UUID;

/**
 * Distributed correlation identifier linking all causally related operations and events.
 */
public record CorrelationId(String value) {

    public CorrelationId {
        Objects.requireNonNull(value, "CorrelationId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("CorrelationId value cannot be blank");
        }
    }

    public static CorrelationId of(String value) {
        return new CorrelationId(value);
    }

    public static CorrelationId generate() {
        return new CorrelationId("corr-" + UUID.randomUUID());
    }
}
