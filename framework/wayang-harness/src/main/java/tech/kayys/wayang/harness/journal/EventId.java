package tech.kayys.wayang.harness.journal;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for an execution event.
 */
public record EventId(String value) {

    public EventId {
        Objects.requireNonNull(value, "EventId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("EventId value cannot be blank");
        }
    }

    public static EventId of(String value) {
        return new EventId(value);
    }

    public static EventId generate() {
        return new EventId("evt-" + UUID.randomUUID());
    }
}
