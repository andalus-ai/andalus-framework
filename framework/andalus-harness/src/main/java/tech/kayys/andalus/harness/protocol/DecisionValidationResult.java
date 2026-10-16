package tech.kayys.andalus.harness.protocol;

import java.util.Objects;

/**
 * Value object representing the outcome of validating an {@link AgentDecision}.
 */
public record DecisionValidationResult(
        boolean valid,
        String reason
) {
    public DecisionValidationResult {
        reason = reason == null ? "" : reason;
    }

    public static DecisionValidationResult accepted() {
        return new DecisionValidationResult(true, "");
    }

    public static DecisionValidationResult rejected(String reason) {
        return new DecisionValidationResult(false, Objects.requireNonNullElse(reason, "Decision rejected"));
    }
}
