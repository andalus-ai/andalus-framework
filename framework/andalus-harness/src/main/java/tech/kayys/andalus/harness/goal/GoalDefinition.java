package tech.kayys.andalus.harness.goal;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Reusable goal definition describing a desired outcome and its satisfaction criteria.
 */
public record GoalDefinition(
        GoalId id,
        String title,
        String description,
        String desiredOutcome,
        Optional<GoalId> parentGoalId,
        List<GoalCriterion> successCriteria,
        Map<String, Object> metadata
) {
    public GoalDefinition {
        Objects.requireNonNull(id, "id is required");
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        description = description == null ? "" : description;
        desiredOutcome = desiredOutcome == null ? "" : desiredOutcome;
        parentGoalId = parentGoalId == null ? Optional.empty() : parentGoalId;
        successCriteria = successCriteria == null ? List.of() : List.copyOf(successCriteria);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static GoalDefinition of(String title, String desiredOutcome) {
        return new GoalDefinition(
                GoalId.generate(),
                title,
                title,
                desiredOutcome,
                Optional.empty(),
                List.of(),
                Map.of()
        );
    }
}
