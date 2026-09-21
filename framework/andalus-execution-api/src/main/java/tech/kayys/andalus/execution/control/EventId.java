package tech.kayys.andalus.execution.control;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a control event.
 */
public record EventId(String value) {
    public EventId {
        Objects.requireNonNull(value, "value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank");
        }
    }

    public static EventId of(String value) {
        return new EventId(value);
    }

    public static EventId generate() {
        return new EventId("evt-" + UUID.randomUUID());
    }
}
