package tech.kayys.andalus.harness.protocol;

/**
 * Defines the agent execution state values used by the Andalus framework.
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
