package tech.kayys.andalus.harness.task;

import tech.kayys.andalus.harness.goal.GoalId;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable read-only projection of a task for use in decision contexts.
 */
public record TaskSnapshot(
        TaskId taskId,
        String title,
        String description,
        Optional<GoalId> goalId,
        TaskState state,
        List<TaskId> dependencies,
        Map<String, Object> input
) {
    public TaskSnapshot {
        Objects.requireNonNull(taskId, "taskId is required");
        Objects.requireNonNull(title, "title is required");
        description = description == null ? "" : description;
        goalId = goalId == null ? Optional.empty() : goalId;
        Objects.requireNonNull(state, "state is required");
        dependencies = dependencies == null ? List.of() : List.copyOf(dependencies);
        input = input == null ? Map.of() : Map.copyOf(input);
    }

    public static TaskSnapshot from(TaskInstance instance) {
        return new TaskSnapshot(
                instance.id(),
                instance.definition().title(),
                instance.definition().description(),
                instance.goalId(),
                instance.state(),
                instance.definition().dependsOn(),
                instance.definition().input()
        );
    }

    public static TaskSnapshot of(String title, String description) {
        return new TaskSnapshot(
                TaskId.generate(),
                title,
                description,
                Optional.empty(),
                TaskState.READY,
                List.of(),
                Map.of()
        );
    }
}
