package tech.kayys.andalus.harness.planning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class ResourceBudgetPlanValidatorTest {

    private final PlanValidator structural = new DefaultPlanValidator();

    @Test
    void acceptsPlanWithinAllConfiguredBudgets() {
        PlanValidator validator = validator(2, "10.00", 5_000L, 1_000L);
        AgentPlan plan = plan(
                step("one", Map.of("estimated_cost", 2.5,
                        "estimated_duration_ms", 1_000, "estimated_tokens", 200)),
                step("two", Map.of("estimated_cost", 3.0,
                        "estimated_duration_ms", 2_000, "estimated_tokens", 300)));

        assertThat(validator.validate(plan).valid()).isTrue();
    }

    @Test
    void rejectsStepCountAndAggregatedResourceBudgetOverages() {
        PlanValidator validator = validator(1, "4.0", 1_500L, 400L);
        AgentPlan plan = plan(
                step("one", Map.of("estimated_cost", 2,
                        "estimated_duration_ms", 1_000, "estimated_tokens", 250)),
                step("two", Map.of("estimated_cost", 3,
                        "estimated_duration_ms", 1_000, "estimated_tokens", 250)));

        PlanValidationResult result = validator.validate(plan);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors()).anyMatch(error -> error.contains("maximum allowed is 1"));
        assertThat(result.errors()).anyMatch(error -> error.contains("estimated cost"));
        assertThat(result.errors()).anyMatch(error -> error.contains("estimated duration"));
        assertThat(result.errors()).anyMatch(error -> error.contains("estimated tokens"));
    }

    @Test
    void treatsMissingEstimatesAsZeroAndAllowsUnrestrictedDimensions() {
        PlanValidator validator = new ResourceBudgetPlanValidator(
                structural, new PlanResourceBudget(2, null, null, null));

        assertThat(validator.validate(plan(step("one", Map.of()))).valid()).isTrue();
    }

    @Test
    void rejectsMalformedAndNegativeEstimates() {
        PlanValidator validator = validator(null, null, null, null);
        AgentPlan plan = plan(
                step("bad-type", Map.of("estimated_cost", "expensive")),
                step("negative", Map.of("estimated_tokens", -1)));

        PlanValidationResult result = validator.validate(plan);

        assertThat(result.valid()).isFalse();
        assertThat(result.errors()).anyMatch(error -> error.contains("non-numeric estimated_cost"));
        assertThat(result.errors()).anyMatch(error -> error.contains("negative estimated_tokens"));
    }

    @Test
    void preservesStructuralValidationErrorsAndWarnings() {
        PlanValidator validator = validator(1, null, null, null);
        AgentPlan empty = new AgentPlan(PlanId.of("plan-empty"), "exec",
                Optional.empty(), 0, PlanStatus.PROPOSED, List.of(), Instant.EPOCH, Map.of());

        assertThat(validator.validate(empty).errors())
                .contains("Plan must contain at least one step.");
    }

    @Test
    void rejectsInvalidBudgetConfiguration() {
        assertThatThrownBy(() -> new PlanResourceBudget(0, null, null, null))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new PlanResourceBudget(null, new BigDecimal("-0.1"), null, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private PlanValidator validator(Integer steps, String cost, Long duration, Long tokens) {
        return new ResourceBudgetPlanValidator(structural, new PlanResourceBudget(
                steps, cost == null ? null : new BigDecimal(cost), duration, tokens));
    }

    private static AgentPlan plan(PlanStep... steps) {
        return new AgentPlan(PlanId.of("plan-budget-test"), "execution-budget-test",
                Optional.empty(), 0, PlanStatus.PROPOSED, List.of(steps), Instant.EPOCH, Map.of());
    }

    private static PlanStep step(String id, Map<String, Object> metadata) {
        return new PlanStep(PlanStepId.of(id), "Execute " + id, Optional.empty(),
                List.of(), "", List.of(), metadata);
    }
}
