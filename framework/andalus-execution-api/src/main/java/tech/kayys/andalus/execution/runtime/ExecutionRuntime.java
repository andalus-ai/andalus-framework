package tech.kayys.andalus.execution.runtime;

public interface ExecutionRuntime {
    RuntimeRecoveryCapabilities capabilities();
    ExecutionHandle start(ExecutionStartRequest request);
    CheckpointHandle checkpoint(ExecutionHandle execution);
    void pause(ExecutionHandle execution);
    void resume(ExecutionHandle execution);
    void terminate(ExecutionHandle execution);
}
