package tech.kayys.wayang.execution.process;

import java.util.Objects;

/**
 * Outcome of evaluating a process execution request against sandbox policies.
 */
public record ProcessDecision(
        DecisionType decision,
        String reason
) {

    public enum DecisionType {
        ALLOW,
        DENY,
        AUDIT,
        ISOLATE
    }

    public ProcessDecision {
        Objects.requireNonNull(decision, "decision cannot be null");
        reason = reason != null ? reason : "";
    }

    public boolean isAllowed() {
        return decision == DecisionType.ALLOW || decision == DecisionType.AUDIT;
    }

    public static ProcessDecision allow(String reason) {
        return new ProcessDecision(DecisionType.ALLOW, reason);
    }

    public static ProcessDecision deny(String reason) {
        return new ProcessDecision(DecisionType.DENY, reason);
    }

    public static ProcessDecision audit(String reason) {
        return new ProcessDecision(DecisionType.AUDIT, reason);
    }
}
