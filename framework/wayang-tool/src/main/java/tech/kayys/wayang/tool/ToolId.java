package tech.kayys.wayang.tool;

import java.util.Objects;

/**
 * Strongly typed identifier for a Tool.
 */
public record ToolId(String value) {
    public ToolId {
        Objects.requireNonNull(value, "ToolId value cannot be null");
    }

    public static ToolId of(String value) {
        return new ToolId(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
