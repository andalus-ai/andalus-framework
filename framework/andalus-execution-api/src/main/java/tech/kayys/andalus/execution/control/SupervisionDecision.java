package tech.kayys.andalus.execution.control;

import java.time.Duration;
import java.util.Objects;

/**
 * Remediation decision determined by an ExecutionSupervisor.
 */
public record SupervisionDecision(
        ControlAction action,
        String reason,
        Duration delay
) {
    public SupervisionDecision {
        Objects.requireNonNull(action, "action cannot be null");
        reason = reason == null ? "" : reason;
        if (delay == null) {
            delay = Duration.ZERO;
        }
    }

    public static SupervisionDecision of(ControlAction action, String reason) {
        return new SupervisionDecision(action, reason, Duration.ZERO);
    }

    public static SupervisionDecision retry(String reason, Duration backoff) {
        return new SupervisionDecision(ControlAction.RETRY, reason, backoff);
    }

    public static SupervisionDecision continueExecution() {
        return new SupervisionDecision(ControlAction.CONTINUE, "Healthy", Duration.ZERO);
    }
}
