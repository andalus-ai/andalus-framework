package tech.kayys.andalus.harness.planning;

import java.util.List;

/**
 * Deterministic, read-only assessment of which plan steps should be considered
 * during a subsequent planning revision.
 *
 * <p>All lists retain the declaration order of the original plan.</p>
 */
public record PlanReplanningAssessment(
        List<PlanStepId> completedStepIds,
        List<PlanStepId> directRetryStepIds,
        List<PlanStepId> dependencyAffectedStepIds,
        List<PlanStepId> skippedStepIds,
        List<PlanStepId> unobservedStepIds,
        List<PlanStepId> stepsRequiringReplan
) {
    public PlanReplanningAssessment {
        completedStepIds = List.copyOf(completedStepIds);
        directRetryStepIds = List.copyOf(directRetryStepIds);
        dependencyAffectedStepIds = List.copyOf(dependencyAffectedStepIds);
        skippedStepIds = List.copyOf(skippedStepIds);
        unobservedStepIds = List.copyOf(unobservedStepIds);
        stepsRequiringReplan = List.copyOf(stepsRequiringReplan);
    }
}
