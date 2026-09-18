package tech.kayys.wayang.harness.kernel;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique logical identifier for a Wayang Harness instance.
 */
public record HarnessId(String value) {

    public HarnessId {
        Objects.requireNonNull(value, "HarnessId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("HarnessId value cannot be blank");
        }
    }

    public static HarnessId of(String value) {
        return new HarnessId(value);
    }

    public static HarnessId generate() {
        return new HarnessId("harness-" + UUID.randomUUID());
    }
}
