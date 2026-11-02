package tech.kayys.andalus.harness.fabric;

import java.time.Duration;
import java.util.List;

/**
 * Provider-neutral at-least-once assignment queue.
 *
 * Implementations must make enqueue idempotency, claim acquisition, claim-generation
 * advancement, and claim mutations atomic across all schedulers/workers. A claim expires
 * when its visibility deadline is reached; the assignment may then be delivered again.
 * External side effects still require application-level idempotency or compensation.
 */
public interface TaskQueue {
    /** Enqueue once for an idempotency key. Repeating the same key and assignment is a no-op. */
    void enqueue(String idempotencyKey, ExecutionAssignment assignment);

    /** Atomically claim up to maxTasks currently available assignments for this worker. */
    List<TaskClaim> claim(WorkerId workerId, int maxTasks, Duration visibilityTimeout);

    /** Acknowledge only the current, unexpired claim owner and generation. */
    boolean acknowledge(TaskClaimToken token);

    /** Release a current claim and make it visible after the supplied delay. */
    boolean negativeAcknowledge(TaskClaimToken token, Duration delay);

    /** Extend visibility only for the current, unexpired claim. */
    boolean extendVisibility(TaskClaimToken token, Duration extension);
}
