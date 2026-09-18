package tech.kayys.wayang.harness.execution.action;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a action id.
 *
 * <p>Its components capture `value`.</p>
 *
 * @param value the value
 */


public record ActionId(String value) {
    public ActionId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Action id must not be blank");
        }
    }

    public static ActionId of(String value) {
        return new ActionId(value);
    }

    public static ActionId generate() {
        return new ActionId("act-" + UUID.randomUUID());
    }
}
