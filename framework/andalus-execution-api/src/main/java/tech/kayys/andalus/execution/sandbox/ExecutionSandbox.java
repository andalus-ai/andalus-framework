package tech.kayys.andalus.execution.sandbox;

/**
 * Fundamental execution boundary allocated to a worker.
 */
public interface ExecutionSandbox {

    SandboxId id();

    SandboxSpec specification();

    SandboxState state();
}
