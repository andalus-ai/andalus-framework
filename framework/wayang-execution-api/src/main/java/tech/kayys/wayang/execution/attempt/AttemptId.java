package tech.kayys.wayang.execution.attempt;

import java.util.Objects;
import java.util.UUID;

public record AttemptId(String value) {
    public AttemptId {
        Objects.requireNonNull(value, "value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank");
        }
    }

    public static AttemptId of(String value) {
        return new AttemptId(value);
    }

    public static AttemptId random() {
        return new AttemptId(UUID.randomUUID().toString());
    }
}
