package tech.kayys.andalus.harness.fabric;

import java.util.List;
import java.util.Objects;

/** Caller-scheduled, bounded worker polling loop. */
public final class WorkerTaskPoller {
    private final TaskQueue queue;
    private final WorkerId workerId;
    private final TaskExecutionHandler handler;
    private final TaskFailurePolicy failurePolicy;
    private final int batchSize;
    private final java.time.Duration visibilityTimeout;

    public WorkerTaskPoller(TaskQueue queue, WorkerId workerId,
                            TaskExecutionHandler handler, TaskFailurePolicy failurePolicy,
                            int batchSize, java.time.Duration visibilityTimeout) {
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
        int deadLettered = 0;
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
                TaskFailureDecision decision = Objects.requireNonNull(
                        failurePolicy.decide(claim.assignment(), failure),
                        "failure policy returned null decision");
                TaskFailureResult result = queue.fail(claim.token(), decision);
                switch (result) {
                    case RETRY_SCHEDULED -> requeued++;
                    case DEAD_LETTERED -> deadLettered++;
                    case LOST_CLAIM -> lost++;
                }
            }
        }
        return new TaskPollResult(claims.size(), completed, requeued, deadLettered, lost);
    }
}
