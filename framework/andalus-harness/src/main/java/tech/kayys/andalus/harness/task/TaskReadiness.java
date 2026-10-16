package tech.kayys.andalus.harness.task;

import java.util.List;

/**
 * Evaluates whether a task is ready to be pursued based on dependencies and constraints.
 */
public record TaskReadiness(
        boolean ready,
        List<TaskId> unmetDependencies,
        String reason
) {
    public TaskReadiness {
        unmetDependencies = unmetDependencies == null ? List.of() : List.copyOf(unmetDependencies);
        reason = reason == null ? "" : reason;
    }

    public static TaskReadiness ofReady() {
        return new TaskReadiness(true, List.of(), "");
    }

    public static TaskReadiness blocked(List<TaskId> unmetDependencies, String reason) {
        return new TaskReadiness(false, unmetDependencies, reason);
    }
}
