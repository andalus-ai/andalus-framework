package tech.kayys.andalus.harness.goal;

import java.util.List;
import java.util.Objects;

/**
 * Result of evaluating goal satisfaction or blockers.
 */
public record GoalEvaluation(
        GoalState proposedState,
        boolean criteriaSatisfied,
        List<String> unmetCriteria,
        String rationale
) {
    public GoalEvaluation {
        Objects.requireNonNull(proposedState, "proposedState is required");
        unmetCriteria = unmetCriteria == null ? List.of() : List.copyOf(unmetCriteria);
        rationale = rationale == null ? "" : rationale;
    }

    public static GoalEvaluation satisfied(String rationale) {
        return new GoalEvaluation(GoalState.SATISFIED, true, List.of(), rationale);
    }

    public static GoalEvaluation blocked(List<String> unmetCriteria, String rationale) {
        return new GoalEvaluation(GoalState.BLOCKED, false, unmetCriteria, rationale);
    }

    public static GoalEvaluation failed(String rationale) {
        return new GoalEvaluation(GoalState.FAILED, false, List.of(), rationale);
    }
}
