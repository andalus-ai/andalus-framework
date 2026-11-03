package tech.kayys.andalus.harness.fabric;

import tech.kayys.andalus.execution.mobility.ExecutionLease;
import tech.kayys.andalus.execution.mobility.FencingToken;
import tech.kayys.andalus.execution.mobility.FencedLeaseManager;
import tech.kayys.andalus.execution.mobility.LeaseManager;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Acquires execution ownership before running work and retains the same fencing
 * token through durable completion. The queue claim token remains a separate
 * fencing domain and is passed unchanged to the completion committer.
 *
 * <p>Long-running workers can opt into host-managed periodic lease renewal.
 * Cooperative work additionally receives an execution context that signals
 * lease loss and supports cancellation callbacks.</p>
 */
public final class ExecutionLeaseBoundTaskExecutionHandler implements TaskExecutionHandler {
    private final LeaseManager leaseManager;
    private final Duration leaseDuration;
    private final FencedTaskExecutionWork work;
    private final CooperativeFencedTaskExecutionWork cooperativeWork;
    private final FencedExecutionCompletionCommitter committer;
    private final ScheduledExecutorService heartbeatScheduler;
    private final Duration heartbeatInterval;

    /** Compatibility constructor for bounded work that completes within the lease duration. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            FencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer) {
        this(leaseManager, leaseDuration, null, null, work, null, committer);
    }

    /** Constructor for bounded work that cooperatively observes lease cancellation. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            CooperativeFencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer) {
        this(leaseManager, leaseDuration, null, null, null, work, committer);
    }

    /** Constructor with host-managed periodic renewal for legacy work callbacks. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            ScheduledExecutorService heartbeatScheduler, Duration heartbeatInterval,
            FencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer) {
        this(leaseManager, leaseDuration, heartbeatScheduler, heartbeatInterval, work, null, committer);
    }

    /** Constructor with host-managed periodic renewal and cooperative cancellation. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            ScheduledExecutorService heartbeatScheduler, Duration heartbeatInterval,
            CooperativeFencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer) {
        this(leaseManager, leaseDuration, heartbeatScheduler, heartbeatInterval, null, work, committer);
    }

    private ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            ScheduledExecutorService heartbeatScheduler, Duration heartbeatInterval,
            FencedTaskExecutionWork work, CooperativeFencedTaskExecutionWork cooperativeWork,
            FencedExecutionCompletionCommitter committer) {
        this.leaseManager = Objects.requireNonNull(leaseManager, "leaseManager cannot be null");
        this.leaseDuration = positive(leaseDuration, "leaseDuration");
        this.heartbeatScheduler = heartbeatScheduler;
        this.heartbeatInterval = heartbeatInterval;
        if ((heartbeatScheduler == null) != (heartbeatInterval == null)) {
            throw new IllegalArgumentException("heartbeatScheduler and heartbeatInterval must be supplied together");
        }
        if (heartbeatInterval != null) {
            positive(heartbeatInterval, "heartbeatInterval");
            if (heartbeatInterval.compareTo(leaseDuration) >= 0) {
                throw new IllegalArgumentException("heartbeatInterval must be shorter than leaseDuration");
            }
        }
        if ((work == null) == (cooperativeWork == null)) {
            throw new IllegalArgumentException("Exactly one work callback must be supplied");
        }
        this.work = work;
        this.cooperativeWork = cooperativeWork;
        this.committer = Objects.requireNonNull(committer, "committer cannot be null");
    }

    private static Duration positive(Duration value, String name) {
        Objects.requireNonNull(value, name + " cannot be null");
        if (value.isZero() || value.isNegative()) {
            throw new IllegalArgumentException(name + " must be positive");
        }
        return value;
    }

    @Override
    public void execute(TaskClaim claim, ClaimVisibility visibility) throws Exception {
        Objects.requireNonNull(claim, "claim cannot be null");
        Objects.requireNonNull(visibility, "visibility cannot be null");

        String executionId = claim.assignment().executionId().value();
        String holderId = claim.token().workerId().value();
        ExecutionLease lease = leaseManager.acquireLease(executionId, holderId, leaseDuration);
        FencingToken executionFence = lease.fencingToken();
        ExecutionLeaseContext leaseContext = new ExecutionLeaseContext();
        ScheduledFuture<?> heartbeat = null;

        try {
            requireCurrentLease(executionId, executionFence, leaseContext);
            if (heartbeatScheduler != null) {
                heartbeat = heartbeatScheduler.scheduleAtFixedRate(
                        () -> renewLease(executionId, holderId, executionFence, leaseContext),
                        heartbeatInterval.toNanos(), heartbeatInterval.toNanos(), TimeUnit.NANOSECONDS);
            }
            if (cooperativeWork != null) {
                cooperativeWork.execute(claim, visibility, executionFence, leaseContext);
            } else {
                work.execute(claim, visibility, executionFence);
            }
            requireCurrentLease(executionId, executionFence, leaseContext);
            committer.commitCompleted(claim.assignment(), claim.token(), executionFence);
        } finally {
            if (heartbeat != null) {
                heartbeat.cancel(false);
            }
            releaseLease(executionId, holderId, executionFence);
        }
    }

    private void renewLease(
            String executionId, String holderId, FencingToken token, ExecutionLeaseContext context) {
        if (context.isCancellationRequested()) {
            return;
        }
        try {
            boolean renewed = leaseManager instanceof FencedLeaseManager fenced
                    ? fenced.renewLease(executionId, holderId, token, leaseDuration)
                    : leaseManager.renewLease(executionId, holderId, leaseDuration);
            if (!renewed) {
                context.cancel();
            }
        } catch (RuntimeException renewalFailure) {
            context.cancel();
        }
    }

    private void releaseLease(String executionId, String holderId, FencingToken token) {
        if (leaseManager instanceof FencedLeaseManager fenced) {
            fenced.releaseLease(executionId, holderId, token);
        } else {
            // Compatibility for legacy providers; distributed providers should implement FencedLeaseManager.
            leaseManager.releaseLease(executionId, holderId);
        }
    }

    private void requireCurrentLease(
            String executionId, FencingToken executionFence, ExecutionLeaseContext context)
            throws StaleExecutionCompletionException {
        if (context.isCancellationRequested() || !leaseManager.validateToken(executionId, executionFence)) {
            context.cancel();
            throw new StaleExecutionCompletionException(
                    "Execution lease expired or was superseded for execution " + executionId
                            + " at fencing generation " + executionFence.generation());
        }
    }
}
