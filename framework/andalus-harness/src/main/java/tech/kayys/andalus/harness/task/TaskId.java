package tech.kayys.andalus.harness.task;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for an agent task.
 */
public record TaskId(String value) {
    public TaskId {
        Objects.requireNonNull(value, "TaskId value cannot be null");
    }

    public static TaskId of(String value) {
        return new TaskId(value);
    }

    public static TaskId generate() {
        return new TaskId("task-" + UUID.randomUUID());
    }
}
