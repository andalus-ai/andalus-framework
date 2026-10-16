package tech.kayys.andalus.harness.goal;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for a goal.
 */
public record GoalId(String value) {
    public GoalId {
        Objects.requireNonNull(value, "GoalId value cannot be null");
    }

    public static GoalId of(String value) {
        return new GoalId(value);
    }

    public static GoalId generate() {
        return new GoalId("goal-" + UUID.randomUUID());
    }
}
