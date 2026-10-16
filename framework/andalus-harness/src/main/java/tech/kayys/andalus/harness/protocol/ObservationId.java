package tech.kayys.andalus.harness.protocol;

import java.util.Objects;
import java.util.UUID;

public record ObservationId(String value) {

    public ObservationId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ObservationId cannot be blank");
        }
    }

    public static ObservationId generate() {
        return new ObservationId("obs-" + UUID.randomUUID());
    }

    public static ObservationId of(String value) {
        return new ObservationId(value);
    }
}
