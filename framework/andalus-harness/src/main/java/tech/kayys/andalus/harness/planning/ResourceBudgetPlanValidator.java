package tech.kayys.andalus.harness.planning;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Decorates a structural {@link PlanValidator} with plan-size and estimated
 * resource-budget checks.
 *
 * <p>Resource estimates are optional numeric values in each step's metadata:
 * {@code estimated_cost}, {@code estimated_duration_ms}, and
 * {@code estimated_tokens}. Values are summed across steps. Missing estimates
 * contribute zero; malformed or negative estimates reject the plan. Cost
 * units are defined by the application and must be consistent within a plan.
 * This validator checks estimates only; it does not meter actual consumption.</p>
 */
public final class ResourceBudgetPlanValidator implements PlanValidator {

    public static final String ESTIMATED_COST = "estimated_cost";
    public static final String ESTIMATED_DURATION_MILLIS = "estimated_duration_ms";
    public static final String ESTIMATED_TOKENS = "estimated_tokens";

    private final PlanValidator delegate;
    private final PlanResourceBudget budget;

    public ResourceBudgetPlanValidator(PlanValidator delegate, PlanResourceBudget budget) {
        this.delegate = Objects.requireNonNull(delegate, "delegate is required");
        this.budget = Objects.requireNonNull(budget, "budget is required");
    }

    @Override
    public PlanValidationResult validate(AgentPlan plan) {
        PlanValidationResult structural = delegate.validate(plan);
        if (plan == null) {
            return structural;
        }

        List<String> errors = new ArrayList<>(structural.errors());
        List<String> warnings = new ArrayList<>(structural.warnings());

        if (budget.maxSteps() != null && plan.steps().size() > budget.maxSteps()) {
            errors.add("Plan contains " + plan.steps().size()
                    + " steps; maximum allowed is " + budget.maxSteps() + ".");
        }

        BigDecimal cost = BigDecimal.ZERO;
        BigDecimal durationMillis = BigDecimal.ZERO;
        BigDecimal tokens = BigDecimal.ZERO;

        for (PlanStep step : plan.steps()) {
            Map<String, Object> metadata = step.metadata();
            cost = accumulate(metadata, ESTIMATED_COST, step, cost, errors);
            durationMillis = accumulate(metadata, ESTIMATED_DURATION_MILLIS, step, durationMillis, errors);
            tokens = accumulate(metadata, ESTIMATED_TOKENS, step, tokens, errors);
        }

        checkLimit("estimated cost", cost, budget.maxEstimatedCost(), errors);
        checkLimit("estimated duration (ms)", durationMillis,
                budget.maxEstimatedDurationMillis() == null ? null
                        : BigDecimal.valueOf(budget.maxEstimatedDurationMillis()), errors);
        checkLimit("estimated tokens", tokens,
                budget.maxEstimatedTokens() == null ? null
                        : BigDecimal.valueOf(budget.maxEstimatedTokens()), errors);

        return errors.isEmpty()
                ? PlanValidationResult.acceptedWithWarnings(warnings)
                : PlanValidationResult.rejected(errors, warnings);
    }

    private static BigDecimal accumulate(
            Map<String, Object> metadata,
            String key,
            PlanStep step,
            BigDecimal total,
            List<String> errors) {
        Object raw = metadata.get(key);
        if (raw == null) {
            return total;
        }
        if (!(raw instanceof Number number)) {
            errors.add("Step '" + step.id().value() + "' has a non-numeric " + key + " estimate.");
            return total;
        }
        try {
            BigDecimal value = new BigDecimal(number.toString());
            if (value.signum() < 0) {
                errors.add("Step '" + step.id().value() + "' has a negative " + key + " estimate.");
                return total;
            }
            return total.add(value);
        } catch (NumberFormatException ex) {
            errors.add("Step '" + step.id().value() + "' has an invalid " + key + " estimate.");
            return total;
        }
    }

    private static void checkLimit(
            String label,
            BigDecimal estimate,
            BigDecimal limit,
            List<String> errors) {
        if (limit != null && estimate.compareTo(limit) > 0) {
            errors.add("Plan " + label + " estimate " + estimate
                    + " exceeds budget " + limit + ".");
        }
    }
}
