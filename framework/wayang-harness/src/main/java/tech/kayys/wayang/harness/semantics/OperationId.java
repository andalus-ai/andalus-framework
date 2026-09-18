package tech.kayys.wayang.harness.semantics;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a semantic operation.
 */
public record OperationId(String value) {

    public OperationId {
        Objects.requireNonNull(value, "OperationId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("OperationId value cannot be blank");
        }
    }

    public static OperationId of(String value) {
        return new OperationId(value);
    }

    public static OperationId generate() {
        return new OperationId("op-" + UUID.randomUUID());
    }
}
