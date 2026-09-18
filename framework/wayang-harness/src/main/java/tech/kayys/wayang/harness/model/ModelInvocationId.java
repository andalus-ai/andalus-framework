package tech.kayys.wayang.harness.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a model invocation id.
 *
 * <p>Its components capture `value`.</p>
 *
 * @param value the value
 */


public record ModelInvocationId(String value) {
    public ModelInvocationId {
        Objects.requireNonNull(value, "ModelInvocationId value cannot be null");
    }

    public static ModelInvocationId of(String value) {
        return new ModelInvocationId(value);
    }

    public static ModelInvocationId generate() {
        return new ModelInvocationId("minv-" + UUID.randomUUID());
    }
}
