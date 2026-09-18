package tech.kayys.wayang.harness.protocol;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a turn id.
 *
 * <p>Its components capture `value`.</p>
 *
 * @param value the value
 */


public record TurnId(String value) {
    public TurnId {
        Objects.requireNonNull(value, "TurnId value cannot be null");
    }

    public static TurnId generate() {
        return new TurnId("turn-" + UUID.randomUUID());
    }

    public static TurnId of(String value) {
        return new TurnId(value);
    }
}
