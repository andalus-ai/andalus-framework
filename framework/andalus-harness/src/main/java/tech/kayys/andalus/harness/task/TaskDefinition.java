package tech.kayys.andalus.harness.task;

import tech.kayys.andalus.harness.goal.GoalId;
import tech.kayys.andalus.harness.protocol.AgentTask;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Definition of a bounded unit of work contributing toward a goal.
 */
public record TaskDefinition(
        TaskId id,
        String title,
        String description,
        Optional<GoalId> goalId,
        List<TaskId> dependsOn,
        Map<String, Object> input,
        Map<String, Object> metadata
) {
    public TaskDefinition {
        Objects.requireNonNull(id, "id is required");
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        description = description == null ? "" : description;
        goalId = goalId == null ? Optional.empty() : goalId;
        dependsOn = dependsOn == null ? List.of() : List.copyOf(dependsOn);
        input = input == null ? Map.of() : Map.copyOf(input);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static TaskDefinition of(String title, String description) {
        return new TaskDefinition(
                TaskId.generate(),
                title,
                description,
                Optional.empty(),
                List.of(),
                Map.of(),
                Map.of()
        );
    }

    public static TaskDefinition fromAgentTask(AgentTask agentTask) {
        return new TaskDefinition(
                TaskId.generate(),
                agentTask.description(),
                agentTask.description(),
                Optional.empty(),
                List.of(),
                agentTask.input(),
                Map.of()
        );
    }

    public AgentTask toAgentTask() {
        return new AgentTask(description, input);
    }
}
