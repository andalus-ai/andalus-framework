package tech.kayys.andalus.harness.planning;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Step-level guidance supplied to an incremental planner during plan revision.
 *
 * <p>Preserved steps are the original successful step objects, not copies.
 * Retry candidates are failed/blocked steps and dependents affected by them.
 * This is advisory input: a planner may propose a different valid plan, while
 * the planning runtime remains responsible for validating the revised plan.</p>
 */
public record PlanRevisionGuidance(
        List<PlanStep> preservedSteps,
        List<PlanStep> stepsToReplan,
        List<PlanStep> dependencyAffectedSteps,
        List<PlanStep> skippedSteps,
        List<PlanStep> unobservedSteps
) {
    public PlanRevisionGuidance {
        preservedSteps = List.copyOf(preservedSteps);
        stepsToReplan = List.copyOf(stepsToReplan);
        dependencyAffectedSteps = List.copyOf(dependencyAffectedSteps);
        skippedSteps = List.copyOf(skippedSteps);
        unobservedSteps = List.copyOf(unobservedSteps);
    }

    /**
     * Builds guidance from an assessment, retaining original plan step objects
     * and the declaration order of the source plan.
     */
    public static PlanRevisionGuidance from(
            AgentPlan plan,
            PlanReplanningAssessment assessment
    ) {
        Objects.requireNonNull(plan, "plan is required");
        Objects.requireNonNull(assessment, "assessment is required");
        Map<PlanStepId, PlanStep> byId = plan.steps().stream()
                .collect(Collectors.toMap(PlanStep::id, Function.identity()));

        return new PlanRevisionGuidance(
                resolve(plan, byId, assessment.completedStepIds()),
                resolve(plan, byId, assessment.stepsRequiringReplan()),
                resolve(plan, byId, assessment.dependencyAffectedStepIds()),
                resolve(plan, byId, assessment.skippedStepIds()),
                resolve(plan, byId, assessment.unobservedStepIds()));
    }

    private static List<PlanStep> resolve(
            AgentPlan plan,
            Map<PlanStepId, PlanStep> byId,
            List<PlanStepId> ids
    ) {
        for (PlanStepId id : ids) {
            if (!byId.containsKey(id)) {
                throw new IllegalArgumentException(
                        "Replanning assessment references a step outside the current plan: " + id);
            }
        }
        return ids.stream().map(byId::get).toList();
    }
}
