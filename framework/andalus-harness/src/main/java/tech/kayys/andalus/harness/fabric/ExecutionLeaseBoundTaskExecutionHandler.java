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
    private final FencedExecutionCompletionCommitter committer;
    private final ScheduledExecutorService heartbeatScheduler;
    private final Duration heartbeatInterval;

    /** Compatibility constructor for bounded work that completes within the lease duration. */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager,
            Duration leaseDuration,
            FencedTaskExecutionWork work,
            FencedExecutionCompletionCommitter committer) {
        this(leaseManager, leaseDuration, null, null, work, committer);
    }

    /**
     * Creates a handler with host-managed periodic lease renewal.
     * The heartbeat interval must be shorter than the lease duration.
     */
    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager,
            Duration leaseDuration,
            ScheduledExecutorService heartbeatScheduler,
            Duration heartbeatInterval,
            FencedTaskExecutionWork work,
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
        this.work = Objects.requireNonNull(work, "work cannot be null");
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
        AtomicBoolean ownershipLost = new AtomicBoolean();
        ScheduledFuture<?> heartbeat = null;

        try {
            requireCurrentLease(executionId, executionFence, ownershipLost);
            if (heartbeatScheduler != null) {
                heartbeat = heartbeatScheduler.scheduleAtFixedRate(
                        () -> renewLease(executionId, holderId, executionFence, ownershipLost),
                        heartbeatInterval.toNanos(), heartbeatInterval.toNanos(), TimeUnit.NANOSECONDS);
            }
            work.execute(claim, visibility, executionFence);
            requireCurrentLease(executionId, executionFence, ownershipLost);
            committer.commitCompleted(claim.assignment(), claim.token(), executionFence);
        } finally {
            if (heartbeat != null) {
                heartbeat.cancel(false);
            }
            releaseLease(executionId, holderId, executionFence);
        }
    }

    private void renewLease(
            String executionId, String holderId, FencingToken token, AtomicBoolean ownershipLost) {
        if (ownershipLost.get()) {
            return;
        }
        try {
            boolean renewed = leaseManager instanceof FencedLeaseManager fenced
                    ? fenced.renewLease(executionId, holderId, token, leaseDuration)
                    : leaseManager.renewLease(executionId, holderId, leaseDuration);
            if (!renewed) {
                ownershipLost.set(true);
            }
        } catch (RuntimeException renewalFailure) {
            // Fail closed: work may finish, but completion cannot be committed.
            ownershipLost.set(true);
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
            String executionId, FencingToken executionFence, AtomicBoolean ownershipLost)
            throws StaleExecutionCompletionException {
        if (ownershipLost.get() || !leaseManager.validateToken(executionId, executionFence)) {
            throw new StaleExecutionCompletionException(
                    "Execution lease expired or was superseded for execution " + executionId
                            + " at fencing generation " + executionFence.generation());
        }
    }
}
