package tech.kayys.andalus.harness.fabric;

import java.time.Duration;

/** Selects the delay before a failed task becomes available for redelivery. */
@FunctionalInterface
public interface TaskFailurePolicy {
    Duration retryDelay(ExecutionAssignment assignment, Exception failure);
}
