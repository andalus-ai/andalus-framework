package tech.kayys.wayang.harness.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a model intent id.
 *
 * <p>Its components capture `value`.</p>
 *
 * @param value the value
 */


public record ModelIntentId(String value) {
    public ModelIntentId {
        Objects.requireNonNull(value, "ModelIntentId value cannot be null");
    }

    public static ModelIntentId of(String value) {
        return new ModelIntentId(value);
    }

    public static ModelIntentId generate() {
        return new ModelIntentId("m-intent-" + UUID.randomUUID());
    }
}
