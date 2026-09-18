package tech.kayys.wayang.tool;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identifier for a tool intent.
 */
public record ToolIntentId(String value) {
    public ToolIntentId {
        Objects.requireNonNull(value, "ToolIntentId value cannot be null");
    }

    public static ToolIntentId of(String value) {
        return new ToolIntentId(value);
    }

    public static ToolIntentId generate() {
        return new ToolIntentId("tint-" + UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value;
    }
}
