package tech.kayys.wayang.harness.execution.action;

public enum ActionExecutionMode {
    PURE,
    IDEMPOTENT,
    AT_MOST_ONCE,
    AT_LEAST_ONCE,
    NON_REPLAYABLE
}
