package tech.kayys.wayang.harness.governance.budget;

import java.util.Map;
import java.util.Objects;

public record BudgetOption(
        String providerId,
        String modelId,
        BudgetAmount estimatedCost,
        Map<String, Object> parameters
) {
    public BudgetOption {
        Objects.requireNonNull(providerId, "providerId");
        Objects.requireNonNull(modelId, "modelId");
        Objects.requireNonNull(estimatedCost, "estimatedCost");
        parameters = parameters == null ? Map.of() : Map.copyOf(parameters);
    }
}
