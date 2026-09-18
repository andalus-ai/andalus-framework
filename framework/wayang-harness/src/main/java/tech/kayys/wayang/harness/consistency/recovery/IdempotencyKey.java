package tech.kayys.wayang.harness.consistency.recovery;

import java.util.Objects;
import java.util.UUID;

/**
 * Idempotency key ensuring at-least-once operations produce deterministic side-effects.
 */
public record IdempotencyKey(String value) {

    public IdempotencyKey {
        Objects.requireNonNull(value, "IdempotencyKey value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("IdempotencyKey value cannot be blank");
        }
    }

    public static IdempotencyKey of(String value) {
        return new IdempotencyKey(value);
    }

    public static IdempotencyKey generate() {
        return new IdempotencyKey("idempotency-" + UUID.randomUUID());
    }
}
