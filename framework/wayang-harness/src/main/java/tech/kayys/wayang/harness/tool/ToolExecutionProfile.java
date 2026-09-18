package tech.kayys.wayang.harness.tool;

import java.time.Duration;
import java.util.Objects;

public record ToolExecutionProfile(
        boolean deterministic,
        boolean readOnly,
        boolean idempotent,
        Duration estimatedDuration,
        ToolKind kind
) {
    public ToolExecutionProfile {
        Objects.requireNonNull(kind, "ToolKind cannot be null");
        if (estimatedDuration == null) {
            estimatedDuration = Duration.ZERO;
        }
    }

    public static ToolExecutionProfile readOnly(ToolKind kind) {
        return new ToolExecutionProfile(true, true, true, Duration.ZERO, kind);
    }

    public static ToolExecutionProfile modifying(ToolKind kind) {
        return new ToolExecutionProfile(false, false, false, Duration.ZERO, kind);
    }

    public static ToolExecutionProfile idempotentModifying(ToolKind kind) {
        return new ToolExecutionProfile(false, false, true, Duration.ZERO, kind);
    }
}
