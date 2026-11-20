package tech.kayys.andalus.harness.planning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Calculates a deterministic structural diff between two plan snapshots. */
public final class PlanRevisionDiffCalculator {
    private PlanRevisionDiffCalculator() {
    }

    /**
     * Compares steps by ID, retaining declaration order from the corresponding
     * before/after plan. A successful step that is removed or changed is reported
     * separately as completed work whose continuity was broken.
     */
    public static PlanRevisionDiff calculate(
            AgentPlan previousPlan,
            AgentPlan revisedPlan,
            List<PlanExecutionFeedback> feedback
    ) {
        if (previousPlan == null || revisedPlan == null) {
            throw new IllegalArgumentException("both plan snapshots are required");
        }
        if (!previousPlan.id().equals(revisedPlan.id())) {
            throw new IllegalArgumentException("plan snapshots must share the same plan ID");
        }

        Map<PlanStepId, PlanStep> previous = index(previousPlan.steps(), "previous");
        Map<PlanStepId, PlanStep> revised = index(revisedPlan.steps(), "revised");
        Set<PlanStepId> succeeded = successfulStepIds(feedback, previous.keySet());

        List<PlanStepId> added = new ArrayList<>();
        List<PlanStepId> removed = new ArrayList<>();
        List<PlanStepId> modified = new ArrayList<>();
        List<PlanStepId> preserved = new ArrayList<>();
        List<PlanStepId> replacedCompleted = new ArrayList<>();

        for (PlanStep step : revisedPlan.steps()) {
            PlanStepId id = step.id();
            if (!previous.containsKey(id)) {
                added.add(id);
            } else if (previous.get(id).equals(step)) {
                preserved.add(id);
            } else {
                modified.add(id);
                if (succeeded.contains(id)) {
                    replacedCompleted.add(id);
                }
            }
        }
        for (PlanStep step : previousPlan.steps()) {
            PlanStepId id = step.id();
            if (!revised.containsKey(id)) {
                removed.add(id);
                if (succeeded.contains(id)) {
                    replacedCompleted.add(id);
                }
            }
        }

        return new PlanRevisionDiff(added, removed, modified, preserved, replacedCompleted);
    }

    private static Map<PlanStepId, PlanStep> index(List<PlanStep> steps, String label) {
        Map<PlanStepId, PlanStep> indexed = new HashMap<>();
        for (PlanStep step : steps) {
            if (step == null) {
                throw new IllegalArgumentException(label + " plan contains a null step");
            }
            if (indexed.putIfAbsent(step.id(), step) != null) {
                throw new IllegalArgumentException(
                        label + " plan contains duplicate step ID: " + step.id());
            }
        }
        return indexed;
    }

    private static Set<PlanStepId> successfulStepIds(
            List<PlanExecutionFeedback> feedback,
            Set<PlanStepId> knownStepIds
    ) {
        Set<PlanStepId> seen = new HashSet<>();
        Set<PlanStepId> succeeded = new HashSet<>();
        if (feedback == null) {
            return succeeded;
        }
        for (PlanExecutionFeedback item : feedback) {
            if (item == null) {
                throw new IllegalArgumentException("feedback must not contain null entries");
            }
            if (!knownStepIds.contains(item.stepId())) {
                throw new IllegalArgumentException(
                        "feedback references a step outside the previous plan: " + item.stepId());
            }
            if (!seen.add(item.stepId())) {
                throw new IllegalArgumentException(
                        "feedback contains duplicate step ID: " + item.stepId());
            }
            if (item.outcome() == PlanExecutionFeedback.Outcome.SUCCEEDED) {
                succeeded.add(item.stepId());
            }
        }
        return succeeded;
    }
}
