package tech.kayys.wayang.execution.sideeffect;

import java.util.Objects;

public record IdempotencyKey(String value) {
    public IdempotencyKey {
        Objects.requireNonNull(value, "value cannot be null");
    }

    public static IdempotencyKey of(String value) {
        return new IdempotencyKey(value);
    }
}
