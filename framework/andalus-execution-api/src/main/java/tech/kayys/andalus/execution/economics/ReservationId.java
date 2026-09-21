package tech.kayys.andalus.execution.economics;

import java.util.Objects;
import java.util.UUID;

/**
 * Identifier for a resource reservation commitment.
 */
public record ReservationId(String value) {

    public ReservationId {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static ReservationId of(String value) {
        return new ReservationId(value);
    }

    public static ReservationId generate() {
        return new ReservationId("res-" + UUID.randomUUID());
    }
}
