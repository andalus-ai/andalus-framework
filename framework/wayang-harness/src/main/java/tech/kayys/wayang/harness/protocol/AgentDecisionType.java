package tech.kayys.wayang.harness.protocol;

/**
 * Defines the agent decision type values used by the Wayang framework.
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
