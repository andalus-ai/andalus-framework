package tech.kayys.andalus.harness.planning;

/**
 * SPI for validating an {@link AgentPlan} before it is accepted by the runtime.
 *
 * <p>Implementations may check structural integrity (unique step IDs, valid
 * dependency references, absence of dependency cycles) as well as domain
 * constraints (maximum step count, allowed action types, etc.).</p>
 */
@FunctionalInterface
public interface PlanValidator {

    /**
     * Validates the given plan.
     *
     * @param plan the plan to validate
     * @return a {@link PlanValidationResult} describing the outcome
     */
    PlanValidationResult validate(AgentPlan plan);
}
