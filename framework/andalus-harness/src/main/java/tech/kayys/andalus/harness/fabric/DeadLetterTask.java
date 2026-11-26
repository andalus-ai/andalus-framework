package tech.kayys.andalus.harness.fabric;

import java.time.Instant;
import java.util.Objects;

/** Immutable dead-letter record retained by the durable task queue. */
public record DeadLetterTask(ExecutionAssignment assignment, int failureCount,
                             String failureCategory, String failureMessage,
                             Instant deadLetteredAt) {
    public DeadLetterTask {
        Objects.requireNonNull(assignment, "assignment cannot be null");
        if (failureCount < 1) throw new IllegalArgumentException("failureCount must be positive");
        if (failureCategory == null || failureCategory.isBlank()) {
            throw new IllegalArgumentException("failureCategory cannot be blank");
        }
        Objects.requireNonNull(deadLetteredAt, "deadLetteredAt cannot be null");
    }
}
