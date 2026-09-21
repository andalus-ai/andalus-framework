package tech.kayys.andalus.state.session;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed, immutable identifier for a durable agent session.
 */
public record SessionId(String value) implements Serializable {

    public SessionId {
        Objects.requireNonNull(value, "SessionId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("SessionId value cannot be blank");
        }
    }

    public static SessionId of(String value) {
        return new SessionId(value);
    }

    public static SessionId generate() {
        return new SessionId("sess-" + UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value;
    }
}
