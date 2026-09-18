package tech.kayys.wayang.harness.tool;

import java.util.Objects;

public record ToolId(String value) {
    public ToolId {
        Objects.requireNonNull(value, "ToolId value cannot be null");
    }

    public static ToolId of(String value) {
        return new ToolId(value);
    }
}
