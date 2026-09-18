package tech.kayys.wayang.harness.information.evidence;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a factual evidence item.
 */
public record EvidenceId(String value) {
    public EvidenceId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("EvidenceId cannot be blank");
        }
    }

    public static EvidenceId of(String value) {
        return new EvidenceId(value);
    }

    public static EvidenceId generate() {
        return new EvidenceId("evid-" + UUID.randomUUID());
    }
}
