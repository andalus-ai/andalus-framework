package tech.kayys.andalus.harness.scheduling.cancellation;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

/**
 * Registry mapping active execution IDs to runtime cancellation signals.
 */
public interface CancellationRegistry {

    void register(ExecutionId executionId, Runnable cancelSignal);

    void unregister(ExecutionId executionId);

    void signal(ExecutionId executionId);
}
