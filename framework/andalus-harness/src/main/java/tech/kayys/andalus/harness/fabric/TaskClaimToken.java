package tech.kayys.andalus.harness.fabric;

import java.util.Objects;

/** Fencing token for one queue claim generation. */
public record TaskClaimToken(AssignmentId assignmentId, WorkerId workerId, long generation) {
    public TaskClaimToken {
        Objects.requireNonNull(assignmentId, "assignmentId cannot be null");
        Objects.requireNonNull(workerId, "workerId cannot be null");
        if (generation < 1) throw new IllegalArgumentException("generation must be positive");
    }
}
