package tech.kayys.wayang.harness.execution.recovery;

import java.util.Map;
import java.util.Objects;

public record RecoveryDecision(
        RecoveryAction action,
        String reason,
        Map<String, Object> parameters
) {
    public RecoveryDecision {
        Objects.requireNonNull(action, "action");
        reason = reason == null ? "" : reason;
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }

    public static RecoveryDecision resume(String reason) {
        return new RecoveryDecision(RecoveryAction.RESUME, reason, Map.of());
    }

    public static RecoveryDecision retry(String reason) {
        return new RecoveryDecision(RecoveryAction.RETRY, reason, Map.of());
    }

    public static RecoveryDecision waitAction(String reason) {
        return new RecoveryDecision(RecoveryAction.WAIT, reason, Map.of());
    }

    public static RecoveryDecision fail(String reason) {
        return new RecoveryDecision(RecoveryAction.FAIL, reason, Map.of());
    }
}
