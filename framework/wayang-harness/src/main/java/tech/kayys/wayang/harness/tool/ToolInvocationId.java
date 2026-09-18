package tech.kayys.wayang.harness.tool;

import java.util.Objects;
import java.util.UUID;

public record ToolInvocationId(String value) {
    public ToolInvocationId {
        Objects.requireNonNull(value, "ToolInvocationId value cannot be null");
    }

    public static ToolInvocationId of(String value) {
        return new ToolInvocationId(value);
    }

    public static ToolInvocationId generate() {
        return new ToolInvocationId("inv-" + UUID.randomUUID());
    }
}
