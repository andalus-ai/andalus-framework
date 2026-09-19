package tech.kayys.wayang.execution.filesystem;

import java.util.Objects;

/**
 * Policy decision on a filesystem operation.
 */
public record AccessDecision(
        DecisionType decision,
        String reason
) {

    public enum DecisionType {
        ALLOW,
        DENY,
        AUDIT,
        REDIRECT
    }

    public AccessDecision {
        Objects.requireNonNull(decision, "decision cannot be null");
        reason = reason != null ? reason : "";
    }

    public boolean isAllowed() {
        return decision == DecisionType.ALLOW || decision == DecisionType.AUDIT;
    }

    public static AccessDecision allow(String reason) {
        return new AccessDecision(DecisionType.ALLOW, reason);
    }

    public static AccessDecision deny(String reason) {
        return new AccessDecision(DecisionType.DENY, reason);
    }

    public static AccessDecision audit(String reason) {
        return new AccessDecision(DecisionType.AUDIT, reason);
    }
}
