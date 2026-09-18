package tech.kayys.wayang.harness.execution.action;

/**
 * Defines the action execution mode values used by the Wayang framework.
 */


public enum ActionExecutionMode {
    PURE,
    IDEMPOTENT,
    AT_MOST_ONCE,
    AT_LEAST_ONCE,
    NON_REPLAYABLE
}
