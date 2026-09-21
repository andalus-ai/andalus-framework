package tech.kayys.andalus.execution.control;

import java.util.Objects;

/**
 * Result of scheduler admitting and assigning execution to target runtime.
 */
public record SchedulingDecision(
        boolean admitted,
        String targetRuntimeId,
        ExecutionPriority priority,
        String reason
) {
    public SchedulingDecision {
        reason = reason == null ? "" : reason;
        if (priority == null) {
            priority = ExecutionPriority.NORMAL;
        }
    }

    public static SchedulingDecision admitted(String runtimeId, ExecutionPriority priority) {
        Objects.requireNonNull(runtimeId, "runtimeId cannot be null");
        return new SchedulingDecision(true, runtimeId, priority, "Admitted by scheduler");
    }

    public static SchedulingDecision rejected(String reason) {
        return new SchedulingDecision(false, null, ExecutionPriority.NORMAL, reason);
    }
}
