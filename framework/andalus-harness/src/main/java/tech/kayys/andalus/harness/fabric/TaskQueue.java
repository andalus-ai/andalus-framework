package tech.kayys.andalus.harness.fabric;

import java.time.Duration;
import java.util.List;

/**
 * Provider-neutral at-least-once assignment queue with atomic failure disposition.
 *
 * <p>Implementations must make enqueue idempotency, claim acquisition, claim-generation
 * advancement, claim mutations, retry scheduling, and dead-letter transition atomic
 * across all schedulers/workers. A claim expires when its visibility deadline is reached;
 * the assignment may then be delivered again. External side effects still require
 * application-level idempotency or compensation.</p>
 */
public interface TaskQueue {
    void enqueue(String idempotencyKey, ExecutionAssignment assignment);

    List<TaskClaim> claim(WorkerId workerId, int maxTasks, Duration visibilityTimeout);

    boolean acknowledge(TaskClaimToken token);

    boolean negativeAcknowledge(TaskClaimToken token, Duration delay);

    boolean extendVisibility(TaskClaimToken token, Duration extension);

    /**
     * Atomically records a failure and either schedules redelivery or moves the task
     * to the dead-letter set. Retryable failures are retried up to assignment.maxRetries;
     * the initial delivery is not counted as a retry.
     */
    TaskFailureResult fail(TaskClaimToken token, TaskFailureDecision decision);

    /** Returns a bounded immutable snapshot of dead-lettered tasks, oldest first. */
    List<DeadLetterTask> listDeadLetters(int limit);
}
