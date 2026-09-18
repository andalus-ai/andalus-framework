package tech.kayys.wayang.harness.protocol;

public enum AgentExecutionState {
    CREATED,
    READY,
    RUNNING,
    WAITING,
    WAITING_FOR_HUMAN,
    DELEGATED,
    CHECKPOINTING,
    INTERRUPTED,
    RECOVERING,
    COMPLETED,
    FAILED,
    CANCELED
}
