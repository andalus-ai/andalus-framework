package tech.kayys.andalus.harness.scheduling.retry;

import tech.kayys.andalus.execution.attempt.ExecutionAttempt;
import tech.kayys.andalus.harness.execution.state.ExecutionId;
import tech.kayys.andalus.harness.scheduling.temporal.Deadline;

import java.time.Instant;
import java.util.Optional;

/**
 * Service coordinating durable retry evaluation, attempt transitions, checkpoint preservation,
 * and deadline bounding.
 */
public interface DurableRetryCoordinator {

    /**
     * Evaluates a failure against retry policy and execution deadline, persisting the
     * resulting attempt state and schedule.
     *
     * @param executionId    the logical execution identity
     * @param currentAttempt the current physical attempt
     * @param failure        the error or exception triggering evaluation
     * @param policy         the retry policy to evaluate
     * @param deadline       the optional hard execution deadline
     * @param now            the current time
     * @return the result of the retry evaluation
     */
    RetryResult evaluateAndSchedule(
            ExecutionId executionId,
            ExecutionAttempt currentAttempt,
            Throwable failure,
            RetryPolicy policy,
            Optional<Deadline> deadline,
            Instant now
    );
}
