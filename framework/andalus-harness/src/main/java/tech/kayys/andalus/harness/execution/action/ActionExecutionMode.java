package tech.kayys.andalus.harness.execution.action;

/**
 * Defines the action execution mode values used by the Andalus framework.
 */


public enum ActionExecutionMode {
    PURE,
    IDEMPOTENT,
    AT_MOST_ONCE,
    AT_LEAST_ONCE,
    NON_REPLAYABLE
}
