package tech.kayys.wayang.harness.fabric;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a disposable execution worker.
 */
public record WorkerId(String value) {

    public WorkerId {
        Objects.requireNonNull(value, "WorkerId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("WorkerId value cannot be blank");
        }
    }

    public static WorkerId of(String value) {
        return new WorkerId(value);
    }

    public static WorkerId generate() {
        return new WorkerId("worker-" + UUID.randomUUID());
    }
}
