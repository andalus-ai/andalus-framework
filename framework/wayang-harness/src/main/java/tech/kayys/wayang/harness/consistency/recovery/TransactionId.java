package tech.kayys.wayang.harness.consistency.recovery;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a scoped runtime transaction.
 */
public record TransactionId(String value) {

    public TransactionId {
        Objects.requireNonNull(value, "TransactionId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("TransactionId value cannot be blank");
        }
    }

    public static TransactionId of(String value) {
        return new TransactionId(value);
    }

    public static TransactionId generate() {
        return new TransactionId("tx-" + UUID.randomUUID());
    }
}
