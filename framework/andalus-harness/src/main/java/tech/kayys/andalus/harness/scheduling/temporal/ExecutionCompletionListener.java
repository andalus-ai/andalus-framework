package tech.kayys.andalus.harness.scheduling.temporal;

import tech.kayys.andalus.harness.execution.state.ExecutionId;

/**
 * Listener interface notified when an execution completes (6.15.5.13).
 */
public interface ExecutionCompletionListener {

    /**
     * Called after an execution reaches a terminal status (COMPLETED, FAILED, CANCELED).
     *
     * @param executionId the execution that completed
     */
    void completed(ExecutionId executionId);
}
