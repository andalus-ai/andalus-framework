package tech.kayys.wayang.harness.kernel;

import java.util.Objects;

/**
 * Unique identifier for a Wayang runtime module.
 */
public record ModuleId(String value) {

    public ModuleId {
        Objects.requireNonNull(value, "ModuleId value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ModuleId value cannot be blank");
        }
    }

    public static ModuleId of(String value) {
        return new ModuleId(value);
    }
}
