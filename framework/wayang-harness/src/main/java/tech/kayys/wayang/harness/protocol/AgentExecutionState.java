package tech.kayys.wayang.harness.protocol;

/**
 * Defines the agent execution state values used by the Wayang framework.
 */


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
