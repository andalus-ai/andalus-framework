package tech.kayys.wayang.execution.failure;

import java.util.Objects;
import java.util.UUID;

public record FailureId(String value) {
    public FailureId {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static FailureId of(String value) {
        return new FailureId(value);
    }

    public static FailureId random() {
        return new FailureId(UUID.randomUUID().toString());
    }
}
