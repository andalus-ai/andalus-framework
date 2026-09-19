package tech.kayys.wayang.execution.sandbox;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable identifier for an execution sandbox.
 */
public record SandboxId(String value) {

    public SandboxId {
        Objects.requireNonNull(value, "SandboxId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("SandboxId cannot be blank");
        }
        value = value.trim();
    }

    public static SandboxId of(String value) {
        return new SandboxId(value);
    }

    public static SandboxId generate() {
        return new SandboxId("sbx-" + UUID.randomUUID());
    }
}
