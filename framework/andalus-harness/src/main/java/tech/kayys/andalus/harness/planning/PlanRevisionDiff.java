package tech.kayys.andalus.harness.planning;

import java.util.List;

/**
 * Immutable, declaration-ordered summary of the structural changes in a plan revision.
 *
 * <p>{@code replacedCompletedStepIds} identifies previously successful steps that were
 * removed or modified. It deliberately does not infer which newly added step replaces
 * any particular completed step.</p>
 */
public record PlanRevisionDiff(
        List<PlanStepId> addedStepIds,
        List<PlanStepId> removedStepIds,
        List<PlanStepId> modifiedStepIds,
        List<PlanStepId> preservedStepIds,
        List<PlanStepId> replacedCompletedStepIds
) {
    public PlanRevisionDiff {
        addedStepIds = List.copyOf(addedStepIds);
        removedStepIds = List.copyOf(removedStepIds);
        modifiedStepIds = List.copyOf(modifiedStepIds);
        preservedStepIds = List.copyOf(preservedStepIds);
        replacedCompletedStepIds = List.copyOf(replacedCompletedStepIds);
    }
}
