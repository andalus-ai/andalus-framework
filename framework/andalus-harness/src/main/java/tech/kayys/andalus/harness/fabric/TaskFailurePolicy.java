package tech.kayys.andalus.harness.fabric;

/** Classifies a handler failure as retryable (with delay) or dead-letterable. */
@FunctionalInterface
public interface TaskFailurePolicy {
    TaskFailureDecision decide(ExecutionAssignment assignment, Exception failure);
}
