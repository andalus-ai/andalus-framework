package tech.kayys.wayang.harness.governance.budget;

import java.util.Objects;
import java.util.Optional;

public record BudgetDecision(
        BudgetDecisionType type,
        String reason,
        Optional<BudgetAmount> remaining
) {
    public BudgetDecision {
        Objects.requireNonNull(type, "type");
        reason = reason == null ? "" : reason;
        remaining = remaining == null ? Optional.empty() : remaining;
    }

    public boolean isAllowed() {
        return type == BudgetDecisionType.ALLOW || type == BudgetDecisionType.WARN;
    }

    public boolean isDenied() {
        return type == BudgetDecisionType.DENY;
    }

    public boolean isWarn() {
        return type == BudgetDecisionType.WARN;
    }

    public static BudgetDecision allow(String reason, BudgetAmount remaining) {
        return new BudgetDecision(BudgetDecisionType.ALLOW, reason, Optional.ofNullable(remaining));
    }

    public static BudgetDecision warn(String reason, BudgetAmount remaining) {
        return new BudgetDecision(BudgetDecisionType.WARN, reason, Optional.ofNullable(remaining));
    }

    public static BudgetDecision deny(String reason) {
        return new BudgetDecision(BudgetDecisionType.DENY, reason, Optional.empty());
    }
}
