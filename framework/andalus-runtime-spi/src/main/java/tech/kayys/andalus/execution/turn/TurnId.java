package tech.kayys.andalus.execution.turn;

import java.util.Objects;
import java.util.UUID;

/**
 * Stable identifier for one logical agent turn.
 */
public record TurnId(String value) {

    public TurnId {
        Objects.requireNonNull(value, "value cannot be null");

        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank");
        }
    }

    public static TurnId generate() {
        return new TurnId(UUID.randomUUID().toString());
    }

    public static TurnId of(String value) {
        return new TurnId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
