
package tech.kayys.andalus.agent.kernel.execution;

public enum AgentExecutionState {

    CREATED,

    RUNNING,

    WAITING_FOR_INPUT,

    WAITING_FOR_APPROVAL,

    PAUSED,

    RESUMING,

    COMPLETED,

    FAILED,

    CANCELLED
}
