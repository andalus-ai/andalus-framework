package tech.kayys.andalus.harness.task;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Result of completing or failing an agent task instance.
 */
public record TaskResult(
        TaskId taskId,
        TaskState state,
        Optional<String> summary,
        Optional<String> artifactId,
        Optional<String> failureCode,
        Instant completedAt,
        Map<String, Object> metadata
) {
    public TaskResult {
        Objects.requireNonNull(taskId, "taskId is required");
        Objects.requireNonNull(state, "state is required");
        summary = summary == null ? Optional.empty() : summary;
        artifactId = artifactId == null ? Optional.empty() : artifactId;
        failureCode = failureCode == null ? Optional.empty() : failureCode;
        completedAt = completedAt == null ? Instant.now() : completedAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static TaskResult success(TaskId taskId, String summary) {
        return new TaskResult(
                taskId,
                TaskState.COMPLETED,
                Optional.ofNullable(summary),
                Optional.empty(),
                Optional.empty(),
                Instant.now(),
                Map.of()
        );
    }

    public static TaskResult failure(TaskId taskId, String failureCode, String summary) {
        return new TaskResult(
                taskId,
                TaskState.FAILED,
                Optional.ofNullable(summary),
                Optional.empty(),
                Optional.ofNullable(failureCode),
                Instant.now(),
                Map.of()
        );
    }
}
