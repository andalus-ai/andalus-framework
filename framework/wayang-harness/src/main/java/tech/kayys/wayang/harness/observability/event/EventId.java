package tech.kayys.wayang.harness.observability.event;

import java.util.Objects;
import java.util.UUID;

/**
 * Stable globally unique identifier for a Wayang runtime event.
 */
public record EventId(String value) {
    public EventId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("EventId cannot be blank");
        }
    }

    public static EventId of(String value) {
        return new EventId(value);
    }

    public static EventId generate() {
        return new EventId("evt-" + UUID.randomUUID());
    }
}
