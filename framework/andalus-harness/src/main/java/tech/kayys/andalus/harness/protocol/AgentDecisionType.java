package tech.kayys.andalus.harness.protocol;

/**
 * Defines the agent decision type values used by the Andalus framework.
 */


public enum AgentDecisionType {
    INFER,
    EXECUTE_TOOL,
    WAIT,
    ASK_HUMAN,
    CHECKPOINT,
    DELEGATE,
    COMPLETE,
    FAIL
}
