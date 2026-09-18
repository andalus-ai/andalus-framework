package tech.kayys.wayang.governance.budget;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a budget option.
 *
 * <p>Its components capture `provider id`, `model id`, `estimated cost`, `parameters`.</p>
 *
 * @param providerId the provider id
 * @param modelId the model id
 * @param estimatedCost the estimated cost
 * @param parameters the parameters
 */


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
