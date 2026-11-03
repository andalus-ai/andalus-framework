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

    /**
     * Acknowledge only the exact current, unexpired claim. Repeating the same token
     * after its acknowledgement succeeded must return true; a different or superseded
     * token must return false, including after a later delivery is claimed.
     */
    boolean acknowledge(TaskClaimToken token);

    boolean negativeAcknowledge(TaskClaimToken token, Duration delay);

    boolean extendVisibility(TaskClaimToken token, Duration extension);

    TaskFailureResult fail(TaskClaimToken token, TaskFailureDecision decision);

    List<DeadLetterTask> listDeadLetters(int limit);
}
