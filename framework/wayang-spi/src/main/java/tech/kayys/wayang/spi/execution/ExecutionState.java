package tech.kayys.wayang.spi.execution;

public enum ExecutionState {

    CREATED,

    QUEUED,

    RUNNING,

    PAUSED,

    WAITING,

    COMPLETED,

    FAILED,

    CANCELLED,

    COMPENSATING,

    COMPENSATED
}
