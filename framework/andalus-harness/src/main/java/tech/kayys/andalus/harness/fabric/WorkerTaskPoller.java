package tech.kayys.andalus.harness.fabric;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * Explicit, caller-scheduled worker polling loop.
 *
 * <p>Claims are processed sequentially and in a bounded batch. A normal handler
 * return means its durable completion transition succeeded; only then is the
 * queue claim acknowledged. Failures are negatively acknowledged using the
 * supplied retry policy. A stale claim is never treated as completed.</p>
 *
 * <p>This class creates no background threads. The hosting worker controls
 * polling cadence, shutdown, concurrency, and process lifecycle.</p>
 */
public final class WorkerTaskPoller {
    private final TaskQueue queue;
    private final WorkerId workerId;
    private final TaskExecutionHandler handler;
    private final TaskFailurePolicy failurePolicy;
    private final int batchSize;
    private final Duration visibilityTimeout;

    public WorkerTaskPoller(TaskQueue queue, WorkerId workerId,
                            TaskExecutionHandler handler, TaskFailurePolicy failurePolicy,
                            int batchSize, Duration visibilityTimeout) {
        this.queue = Objects.requireNonNull(queue, "queue cannot be null");
        this.workerId = Objects.requireNonNull(workerId, "workerId cannot be null");
        this.handler = Objects.requireNonNull(handler, "handler cannot be null");
        this.failurePolicy = Objects.requireNonNull(failurePolicy, "failurePolicy cannot be null");
        if (batchSize < 1) throw new IllegalArgumentException("batchSize must be positive");
        this.batchSize = batchSize;
        this.visibilityTimeout = Objects.requireNonNull(visibilityTimeout, "visibilityTimeout cannot be null");
        if (visibilityTimeout.isZero() || visibilityTimeout.isNegative()) {
            throw new IllegalArgumentException("visibilityTimeout must be positive");
        }
    }

    /** Claims and processes at most the configured batch size. */
    public TaskPollResult pollOnce() {
        List<TaskClaim> claims = queue.claim(workerId, batchSize, visibilityTimeout);
        int completed = 0;
        int requeued = 0;
        int lost = 0;

        for (TaskClaim claim : claims) {
            if (!workerId.equals(claim.token().workerId())) {
                lost++;
                continue;
            }
            try {
                handler.execute(claim, extension -> queue.extendVisibility(claim.token(), extension));
                if (queue.acknowledge(claim.token())) completed++;
                else lost++;
            } catch (Exception failure) {
                Duration delay = Objects.requireNonNull(
                        failurePolicy.retryDelay(claim.assignment(), failure),
                        "failure policy returned null delay");
                if (delay.isNegative()) {
                    throw new IllegalStateException("failure policy returned a negative delay", failure);
                }
                if (queue.negativeAcknowledge(claim.token(), delay)) requeued++;
                else lost++;
            }
        }
        return new TaskPollResult(claims.size(), completed, requeued, lost);
    }
}
