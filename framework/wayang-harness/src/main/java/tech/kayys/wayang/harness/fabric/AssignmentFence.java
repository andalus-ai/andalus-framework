package tech.kayys.wayang.harness.fabric;

import java.util.Objects;

/**
 * Fencing token attached to assignments to prevent split-brain execution across worker reconnects.
 */
public record AssignmentFence(
        AssignmentId assignmentId,
        long generation
) {

    public AssignmentFence {
        Objects.requireNonNull(assignmentId, "AssignmentId cannot be null");
    }

    public static AssignmentFence of(AssignmentId assignmentId, long generation) {
        return new AssignmentFence(assignmentId, generation);
    }

    public boolean isValid(long currentGeneration) {
        return this.generation == currentGeneration;
    }
}
