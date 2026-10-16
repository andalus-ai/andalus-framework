package tech.kayys.andalus.harness.goal;

import java.util.List;
import java.util.Objects;

/**
 * Immutable read-only projection of a goal for use in decision contexts.
 */
public record GoalSnapshot(
        GoalId goalId,
        String title,
        String desiredOutcome,
        List<GoalCriterion> successCriteria,
        GoalState state
) {
    public GoalSnapshot {
        Objects.requireNonNull(goalId, "goalId is required");
        Objects.requireNonNull(title, "title is required");
        desiredOutcome = desiredOutcome == null ? "" : desiredOutcome;
        successCriteria = successCriteria == null ? List.of() : List.copyOf(successCriteria);
        Objects.requireNonNull(state, "state is required");
    }

    public static GoalSnapshot from(GoalInstance instance) {
        return new GoalSnapshot(
                instance.id(),
                instance.definition().title(),
                instance.definition().desiredOutcome(),
                instance.definition().successCriteria(),
                instance.state()
        );
    }

    public static GoalSnapshot of(String title, String desiredOutcome) {
        return new GoalSnapshot(
                GoalId.generate(),
                title,
                desiredOutcome,
                List.of(),
                GoalState.ACTIVE
        );
    }
}
