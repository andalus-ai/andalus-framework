package tech.kayys.wayang.harness.governance.budget;

import java.util.Map;
import java.util.Objects;

/**
 * Represents a budget context.
 *
 * <p>Its components capture `execution id`, `attributes`.</p>
 *
 * @param executionId the execution id
 * @param attributes the attributes
 */


public record BudgetContext(
        String executionId,
        Map<String, Object> attributes
) {
    public BudgetContext {
        Objects.requireNonNull(executionId, "executionId");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static BudgetContext of(String executionId) {
        return new BudgetContext(executionId, Map.of());
    }
}
