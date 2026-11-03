package tech.kayys.andalus.harness.fabric;

import tech.kayys.andalus.execution.mobility.ExecutionLease;
import tech.kayys.andalus.execution.mobility.FencingToken;
import tech.kayys.andalus.execution.mobility.FencedLeaseManager;
import tech.kayys.andalus.execution.mobility.LeaseManager;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Acquires execution ownership before running work and retains the same fencing
 * token through durable completion. The queue claim token remains a separate
 * fencing domain and is passed unchanged to the completion committer.
 *
 * <p>Long-running workers can opt into lease renewal by supplying a
 * host-managed scheduler and heartbeat interval. The scheduler is never
 * created or shut down by this handler.</p>
 */
public final class ExecutionLeaseBoundTaskExecutionHandler implements TaskExecutionHandler {
    private final LeaseManager leaseManager;
    private final Duration leaseDuration;
    private final FencedTaskExecutionWork work;
    private final CooperativeFencedTaskExecutionWork cooperativeWork;
    private final FencedExecutionCompletionCommitter committer;
    private final ScheduledExecutorService heartbeatScheduler;
    private final Duration heartbeatInterval;
    private final ExecutionLeaseObserver observer;

    /** Compatibility constructor for bounded work that completes within the lease duration. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            FencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer) {
        this(leaseManager, leaseDuration, null, null, work, null, committer, ExecutionLeaseObserver.NOOP);
    }

    /** Constructor for bounded work that cooperatively observes lease cancellation. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            CooperativeFencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer) {
        this(leaseManager, leaseDuration, null, null, null, work, committer, ExecutionLeaseObserver.NOOP);
    }

    /** Constructor with host-managed periodic renewal for legacy work callbacks. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            ScheduledExecutorService heartbeatScheduler, Duration heartbeatInterval,
            FencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer) {
        this(leaseManager, leaseDuration, heartbeatScheduler, heartbeatInterval,
                work, null, committer, ExecutionLeaseObserver.NOOP);
    }

    /** Constructor with host-managed periodic renewal and cooperative cancellation. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            ScheduledExecutorService heartbeatScheduler, Duration heartbeatInterval,
            CooperativeFencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer) {
        this(leaseManager, leaseDuration, heartbeatScheduler, heartbeatInterval,
                null, work, committer, ExecutionLeaseObserver.NOOP);
    }

    /** Constructor with lease observations for bounded legacy work. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            FencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer,
            ExecutionLeaseObserver observer) {
        this(leaseManager, leaseDuration, null, null, work, null, committer, observer);
    }

    /** Constructor with lease observations for cooperative work. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            CooperativeFencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer,
            ExecutionLeaseObserver observer) {
        this(leaseManager, leaseDuration, null, null, null, work, committer, observer);
    }

    /** Constructor with host-managed renewal and lease observations. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            ScheduledExecutorService heartbeatScheduler, Duration heartbeatInterval,
            FencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer,
            ExecutionLeaseObserver observer) {
        this(leaseManager, leaseDuration, heartbeatScheduler, heartbeatInterval,
                work, null, committer, observer);
    }

    /** Constructor with host-managed renewal, cooperative cancellation, and observations. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            ScheduledExecutorService heartbeatScheduler, Duration heartbeatInterval,
            CooperativeFencedTaskExecutionWork work, FencedExecutionCompletionCommitter committer,
            ExecutionLeaseObserver observer) {
        this(leaseManager, leaseDuration, heartbeatScheduler, heartbeatInterval,
                null, work, committer, observer);
    }

    private ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager, Duration leaseDuration,
            ScheduledExecutorService heartbeatScheduler, Duration heartbeatInterval,
            FencedTaskExecutionWork work, CooperativeFencedTaskExecutionWork cooperativeWork,
            FencedExecutionCompletionCommitter committer, ExecutionLeaseObserver observer) {
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
        this.observer = observer == null ? ExecutionLeaseObserver.NOOP : observer;
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
        AtomicBoolean ownershipLost = new AtomicBoolean();
        ExecutionLeaseContext leaseContext = cooperativeWork == null ? null : new ExecutionLeaseContext();
        ScheduledFuture<?> heartbeat = null;
        observe(ExecutionLeaseObservation.Type.ACQUIRED, executionId, executionFence, null, null);

        try {
            requireCurrentLease(executionId, executionFence, ownershipLost, leaseContext);
            if (heartbeatScheduler != null) {
                heartbeat = heartbeatScheduler.scheduleAtFixedRate(
                        () -> renewLease(executionId, holderId, executionFence, ownershipLost, leaseContext),
                        heartbeatInterval.toNanos(), heartbeatInterval.toNanos(), TimeUnit.NANOSECONDS);
            }
            if (cooperativeWork != null) {
                cooperativeWork.execute(claim, visibility, executionFence, leaseContext);
            } else {
                work.execute(claim, visibility, executionFence);
            }
            requireCurrentLease(executionId, executionFence, ownershipLost, leaseContext);
            committer.commitCompleted(claim.assignment(), claim.token(), executionFence);
        } finally {
            if (heartbeat != null) {
                heartbeat.cancel(false);
            }
            releaseLease(executionId, holderId, executionFence);
        }
    }

    private void renewLease(
            String executionId, String holderId, FencingToken token,
            AtomicBoolean ownershipLost, ExecutionLeaseContext leaseContext) {
        if (ownershipLost.get()) {
            return;
        }
        long started = System.nanoTime();
        try {
            boolean renewed = leaseManager instanceof FencedLeaseManager fenced
                    ? fenced.renewLease(executionId, holderId, token, leaseDuration)
                    : leaseManager.renewLease(executionId, holderId, leaseDuration);
            Duration elapsed = Duration.ofNanos(Math.max(0, System.nanoTime() - started));
            observe(renewed ? ExecutionLeaseObservation.Type.RENEWAL_SUCCEEDED
                    : ExecutionLeaseObservation.Type.RENEWAL_REJECTED, executionId, token, elapsed, null);
            if (!renewed) {
                loseOwnership(executionId, token, ownershipLost, leaseContext, null);
            }
        } catch (RuntimeException renewalFailure) {
            Duration elapsed = Duration.ofNanos(Math.max(0, System.nanoTime() - started));
            observe(ExecutionLeaseObservation.Type.RENEWAL_FAILED, executionId, token,
                    elapsed, renewalFailure.getClass().getName());
            loseOwnership(executionId, token, ownershipLost, leaseContext,
                    renewalFailure.getClass().getName());
        }
    }

    private void loseOwnership(
            String executionId, FencingToken token, AtomicBoolean ownershipLost,
            ExecutionLeaseContext leaseContext, String failureType) {
        if (ownershipLost.compareAndSet(false, true)) {
            observe(ExecutionLeaseObservation.Type.OWNERSHIP_LOST, executionId, token, null, failureType);
            if (leaseContext != null) {
                leaseContext.cancel();
            }
        }
    }

    private void releaseLease(String executionId, String holderId, FencingToken token) {
        boolean released;
        if (leaseManager instanceof FencedLeaseManager fenced) {
            released = fenced.releaseLease(executionId, holderId, token);
        } else {
            // Compatibility for legacy providers; distributed providers should implement FencedLeaseManager.
            leaseManager.releaseLease(executionId, holderId);
            released = true;
        }
        if (released) {
            observe(ExecutionLeaseObservation.Type.RELEASED, executionId, token, null, null);
        }
    }

    private void requireCurrentLease(
            String executionId, FencingToken executionFence, AtomicBoolean ownershipLost,
            ExecutionLeaseContext leaseContext) throws StaleExecutionCompletionException {
        if (ownershipLost.get() || !leaseManager.validateToken(executionId, executionFence)) {
            loseOwnership(executionId, executionFence, ownershipLost, leaseContext, null);
            throw new StaleExecutionCompletionException(
                    "Execution lease expired or was superseded for execution " + executionId
                            + " at fencing generation " + executionFence.generation());
        }
    }

    private void observe(
            ExecutionLeaseObservation.Type type, String executionId, FencingToken token,
            Duration renewalDuration, String failureType) {
        try {
            observer.onObservation(new ExecutionLeaseObservation(
                    type, executionId, token.generation(), Instant.now(), renewalDuration, failureType));
        } catch (RuntimeException ignored) {
            // Observability must never alter lease safety or task completion.
        }
    }
}
