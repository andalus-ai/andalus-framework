package tech.kayys.wayang.harness.model;

import java.util.Objects;

public record ModelId(String value) {
    public ModelId {
        Objects.requireNonNull(value, "ModelId value cannot be null");
    }

    public static ModelId of(String value) {
        return new ModelId(value);
    }
}
