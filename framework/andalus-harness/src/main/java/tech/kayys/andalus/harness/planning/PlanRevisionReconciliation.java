package tech.kayys.andalus.harness.planning;

import java.util.List;

/**
 * Describes continuity issues between completed work and a proposed plan revision.
 *
 * <p>A preserved step is considered continuous only when the revised plan contains
 * an equal {@link PlanStep} with the same ID and execution semantics.</p>
 */
public record PlanRevisionReconciliation(
        List<PlanStepId> missingPreservedStepIds,
        List<PlanStepId> modifiedPreservedStepIds
) {
    public PlanRevisionReconciliation {
        missingPreservedStepIds = missingPreservedStepIds == null
                ? List.of() : List.copyOf(missingPreservedStepIds);
        modifiedPreservedStepIds = modifiedPreservedStepIds == null
                ? List.of() : List.copyOf(modifiedPreservedStepIds);
    }

    public boolean consistent() {
        return missingPreservedStepIds.isEmpty() && modifiedPreservedStepIds.isEmpty();
    }

    public List<String> errors() {
        java.util.ArrayList<String> errors = new java.util.ArrayList<>();
        missingPreservedStepIds.forEach(id ->
                errors.add("Completed step '" + id + "' is missing from the revised plan."));
        modifiedPreservedStepIds.forEach(id ->
                errors.add("Completed step '" + id + "' was modified in the revised plan."));
        return List.copyOf(errors);
    }
}
