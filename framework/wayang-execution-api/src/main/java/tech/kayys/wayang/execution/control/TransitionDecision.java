package tech.kayys.wayang.execution.control;

import java.util.Objects;

/**
 * Result of validating whether a ControlAction is permitted from the current lifecycle state.
 */
public record TransitionDecision(
        boolean allowed,
        ExecutionLifecycleState targetState,
        String reason
) {
    public TransitionDecision {
        reason = reason == null ? "" : reason;
    }

    public static TransitionDecision allow(ExecutionLifecycleState target) {
        return new TransitionDecision(true, target, "Allowed transition");
    }

    public static TransitionDecision deny(String reason) {
        return new TransitionDecision(false, null, reason);
    }
}
