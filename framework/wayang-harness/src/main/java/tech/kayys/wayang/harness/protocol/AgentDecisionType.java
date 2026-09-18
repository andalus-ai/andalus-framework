package tech.kayys.wayang.harness.protocol;

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
