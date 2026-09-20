package tech.kayys.wayang.execution.economics;

import tech.kayys.wayang.execution.resource.model.ResourceScope;
import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.util.Objects;

/**
 * Economic balance summarizing resource consumption against limits for a scope.
 */
public record ResourceBalance(
    ResourceScope scope,
    ResourceType type,
    double totalConsumed,
    double quotaRemaining,
    double budgetRemaining
) {
    public ResourceBalance {
        Objects.requireNonNull(scope, "scope cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
    }

    public boolean isExhausted() {
        return quotaRemaining <= 0 || budgetRemaining <= 0;
    }
}
