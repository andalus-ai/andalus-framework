package tech.kayys.andalus.harness.planning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Checks whether a proposed revision retains completed steps supplied as
 * preserved-step guidance.
 *
 * <p>This is a continuity check, not a planner and not an executor. It does
 * not require failed, skipped, or unobserved steps to retain their old IDs.
 * Structural dependency validity remains the responsibility of
 * {@link PlanValidator}.</p>
 */
public final class PlanRevisionReconciler {

    public PlanRevisionReconciliation reconcile(
            PlanRevisionRequest request,
            AgentPlan revisedPlan
    ) {
        Objects.requireNonNull(request, "request is required");
        Objects.requireNonNull(revisedPlan, "revisedPlan is required");

        Map<PlanStepId, PlanStep> revisedById = new HashMap<>();
        for (PlanStep step : revisedPlan.steps()) {
            Objects.requireNonNull(step, "revised plan must not contain null steps");
            if (revisedById.putIfAbsent(step.id(), step) != null) {
                throw new IllegalArgumentException("Revised plan contains duplicate step ID: " + step.id());
            }
        }

        List<PlanStepId> missing = new java.util.ArrayList<>();
        List<PlanStepId> modified = new java.util.ArrayList<>();
        for (PlanStep completed : request.replanningGuidance().preservedSteps()) {
            PlanStep candidate = revisedById.get(completed.id());
            if (candidate == null) {
                missing.add(completed.id());
            } else if (!completed.equals(candidate)) {
                modified.add(completed.id());
            }
        }
        return new PlanRevisionReconciliation(missing, modified);
    }
}
