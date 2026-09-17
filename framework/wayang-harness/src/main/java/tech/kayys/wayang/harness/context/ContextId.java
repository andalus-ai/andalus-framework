package tech.kayys.wayang.harness.context;

import java.util.Objects;
import java.util.UUID;

public record ContextId(String value) {
    public ContextId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Context id must not be blank");
        }
    }

    public static ContextId of(String value) {
        return new ContextId(value);
    }

    public static ContextId generate() {
        return new ContextId("ctx-" + UUID.randomUUID());
    }
}
