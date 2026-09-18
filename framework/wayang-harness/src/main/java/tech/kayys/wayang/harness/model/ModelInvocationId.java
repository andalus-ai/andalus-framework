package tech.kayys.wayang.harness.model;

import java.util.Objects;
import java.util.UUID;

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
