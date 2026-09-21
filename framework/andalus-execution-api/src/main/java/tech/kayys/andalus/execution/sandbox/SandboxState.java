package tech.kayys.andalus.execution.sandbox;

/**
 * Lifecycle states of an execution sandbox.
 */
public enum SandboxState {
    CREATING,
    READY,
    RUNNING,
    PAUSED,
    CHECKPOINTING,
    STOPPING,
    DESTROYED,
    FAILED,
    QUARANTINED
}
