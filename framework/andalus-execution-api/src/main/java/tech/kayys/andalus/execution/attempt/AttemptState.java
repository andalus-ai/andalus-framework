package tech.kayys.andalus.execution.attempt;

public enum AttemptState {
    INITIALIZING,
    STARTING,
    RUNNING,
    CHECKPOINTING,
    PAUSED,
    COMPLETED,
    FAILED,
    LOST,
    PREEMPTED
}
