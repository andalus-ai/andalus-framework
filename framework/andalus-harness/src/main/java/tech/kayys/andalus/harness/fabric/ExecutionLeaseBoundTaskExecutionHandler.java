package tech.kayys.andalus.harness.fabric;

import tech.kayys.andalus.execution.mobility.ExecutionLease;
import tech.kayys.andalus.execution.mobility.FencingToken;
import tech.kayys.andalus.execution.mobility.LeaseManager;

import java.time.Duration;
import java.util.Objects;

/**
 * Acquires execution ownership before running work and retains the same fencing
 * token through durable completion. The queue claim token remains a separate
 * fencing domain and is passed unchanged to the completion committer.
 *
 * <p>The lease duration must exceed the bounded work duration. Long-running
 * workers must renew the lease through a host-managed heartbeat before expiry;
 * this handler deliberately does not create background threads.</p>
 */
public final class ExecutionLeaseBoundTaskExecutionHandler implements TaskExecutionHandler {
    private final LeaseManager leaseManager;
    private final Duration leaseDuration;
    private final FencedTaskExecutionWork work;
    private final FencedExecutionCompletionCommitter committer;

    public ExecutionLeaseBoundTaskExecutionHandler(
            LeaseManager leaseManager,
            Duration leaseDuration,
            FencedTaskExecutionWork work,
            FencedExecutionCompletionCommitter committer) {
        this.leaseManager = Objects.requireNonNull(leaseManager, "leaseManager cannot be null");
        this.leaseDuration = Objects.requireNonNull(leaseDuration, "leaseDuration cannot be null");
        if (leaseDuration.isZero() || leaseDuration.isNegative()) {
            throw new IllegalArgumentException("leaseDuration must be positive");
        }
        this.work = Objects.requireNonNull(work, "work cannot be null");
        this.committer = Objects.requireNonNull(committer, "committer cannot be null");
    }

    @Override
    public void execute(TaskClaim claim, ClaimVisibility visibility) throws Exception {
        Objects.requireNonNull(claim, "claim cannot be null");
        Objects.requireNonNull(visibility, "visibility cannot be null");

        String executionId = claim.assignment().executionId().value();
        String holderId = claim.token().workerId().value();
        ExecutionLease lease = leaseManager.acquireLease(executionId, holderId, leaseDuration);
        FencingToken executionFence = lease.fencingToken();

        try {
            requireCurrentLease(executionId, executionFence);
            work.execute(claim, visibility, executionFence);
            requireCurrentLease(executionId, executionFence);
            committer.commitCompleted(claim.assignment(), claim.token(), executionFence);
        } finally {
            leaseManager.releaseLease(executionId, holderId);
        }
    }

    private void requireCurrentLease(String executionId, FencingToken executionFence)
            throws StaleExecutionCompletionException {
        if (!leaseManager.validateToken(executionId, executionFence)) {
            throw new StaleExecutionCompletionException(
                    "Execution lease expired or was superseded for execution " + executionId
                            + " at fencing generation " + executionFence.generation());
        }
    }
}
