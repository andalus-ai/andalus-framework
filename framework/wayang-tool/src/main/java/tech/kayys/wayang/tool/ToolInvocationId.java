package tech.kayys.wayang.tool;

import java.util.Objects;
import java.util.UUID;

/**
 * Strongly typed identifier for a tool invocation instance.
 */
public record ToolInvocationId(String value) {
    public ToolInvocationId {
        Objects.requireNonNull(value, "ToolInvocationId value cannot be null");
    }

    public static ToolInvocationId of(String value) {
        return new ToolInvocationId(value);
    }

    public static ToolInvocationId generate() {
        return new ToolInvocationId("tinv-" + UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value;
    }
}
