package tech.kayys.andalus.harness.goal;

import java.util.Map;
import java.util.Objects;

/**
 * Criterion representing a verifiable condition or human judgment requirement for goal satisfaction.
 */
public record GoalCriterion(
        String id,
        String description,
        boolean mandatory,
        Map<String, Object> metadata
) {
    public GoalCriterion {
        Objects.requireNonNull(id, "id is required");
        Objects.requireNonNull(description, "description is required");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static GoalCriterion of(String id, String description) {
        return new GoalCriterion(id, description, true, Map.of());
    }

    public static GoalCriterion of(String id, String description, boolean mandatory) {
        return new GoalCriterion(id, description, mandatory, Map.of());
    }
}
