package tech.kayys.andalus.harness.reflection;

/**
 * Trigger condition initiating a reflection operation.
 */
public enum ReflectionTrigger {
    ACTION_FAILURE,
    TASK_COMPLETION,
    GOAL_ASSESSMENT,
    PLAN_DEVIATION,
    CONTRADICTING_EVIDENCE,
    HUMAN_CORRECTION,
    EXECUTION_COMPLETION,
    EXPLICIT_REQUEST
}
