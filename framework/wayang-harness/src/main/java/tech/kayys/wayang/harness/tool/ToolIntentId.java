package tech.kayys.wayang.harness.tool;

import java.util.Objects;
import java.util.UUID;

/**
 * Represents a tool intent id.
 *
 * <p>Its components capture `value`.</p>
 *
 * @param value the value
 */


public record ToolIntentId(String value) {
    public ToolIntentId {
        Objects.requireNonNull(value, "ToolIntentId value cannot be null");
    }

    public static ToolIntentId of(String value) {
        return new ToolIntentId(value);
    }

    public static ToolIntentId generate() {
        return new ToolIntentId("intent-" + UUID.randomUUID());
    }
}
