package tech.kayys.wayang.agent.orchestration.coordination;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a distributed synchronization barrier.
 */
public record BarrierId(String value) implements Serializable {

    public BarrierId {
        Objects.requireNonNull(value, "BarrierId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("BarrierId value cannot be blank");
        }
    }

    public static BarrierId of(String value) {
        return new BarrierId(value);
    }

    public static BarrierId generate() {
        return new BarrierId("barr-" + UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value;
    }
}
