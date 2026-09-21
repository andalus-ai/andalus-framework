package tech.kayys.andalus.harness.execution.lifecycle;

import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.scheduling.cancellation.CancellationHandle;
import tech.kayys.andalus.harness.scheduling.cancellation.CancellationMode;
import tech.kayys.andalus.harness.scheduling.temporal.Deadline;

import java.time.Instant;

/**
 * Service managing durable execution lifecycle boundaries: suspend, resume,
 * cancellation, and timeout enforcement.
 */
public interface DurableExecutionControl {

    /**
     * Durably suspends a running execution by capturing a checkpoint,
     * recording an audit event, and persisting the SUSPENDED state.
     *
     * @param executionId the execution to suspend
     */
    void suspend(ExecutionId executionId);

    /**
     * Durably resumes a suspended execution from its latest checkpoint.
     *
     * @param executionId the execution to resume
     */
    void resume(ExecutionId executionId);

    /**
     * Durably cancels an execution according to the requested cancellation mode.
     *
     * @param executionId the execution to cancel
     * @param mode        the cancellation mode
     * @param reason      the human-readable or operational reason
     * @return a handle representing the cancellation outcome
     */
    CancellationHandle cancel(ExecutionId executionId, CancellationMode mode, String reason);

    /**
     * Checks if the execution has reached or exceeded its deadline,
     * transitioning it to TIMEOUT if expired.
     *
     * @param executionId the execution to check
     * @param deadline    the configured deadline
     * @param now         the current time
     * @return true if timed out, false otherwise
     */
    boolean checkTimeout(ExecutionId executionId, Deadline deadline, Instant now);
}
