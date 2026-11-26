package tech.kayys.andalus.execution.core.recovery;

import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.attempt.AttemptState;
import tech.kayys.andalus.execution.attempt.ExecutionAttempt;
import tech.kayys.andalus.execution.attempt.ExecutionAttemptStore;
import tech.kayys.andalus.execution.mobility.ExecutionDurableState;
import tech.kayys.andalus.execution.mobility.ExecutionLease;
import tech.kayys.andalus.execution.mobility.ExecutionStatus;
import tech.kayys.andalus.execution.mobility.LeaseManager;
import tech.kayys.andalus.execution.mobility.state.ExecutionStateStore;
import tech.kayys.andalus.execution.recovery.RecoveryReconciliationAction;
import tech.kayys.andalus.execution.recovery.RecoveryReconciliationResult;
import tech.kayys.andalus.execution.recovery.RecoveryReconciler;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

/**
 * Repairs incomplete recovery transitions after the previous recovery lease expires.
 *
 * <p>This component deliberately does not rerun checkpoint restoration. It repairs
 * durable bookkeeping only: an interrupted attempt is fenced as LOST and the logical
 * execution is returned to SUSPENDED, or a completed restoration is promoted to RUNNING
 * when the attempt write succeeded but the execution-status write did not.</p>
 */
public class DefaultRecoveryReconciler implements RecoveryReconciler {

    private final LeaseManager leaseManager;
    private final ExecutionStateStore stateStore;
    private final ExecutionAttemptStore attemptStore;
    private final String reconcilerWorkerId;
    private final Duration leaseDuration;

    public DefaultRecoveryReconciler(LeaseManager leaseManager, ExecutionStateStore stateStore,
            ExecutionAttemptStore attemptStore, String reconcilerWorkerId, Duration leaseDuration) {
        this.leaseManager = Objects.requireNonNull(leaseManager, "leaseManager cannot be null");
        this.stateStore = Objects.requireNonNull(stateStore, "stateStore cannot be null");
        this.attemptStore = Objects.requireNonNull(attemptStore, "attemptStore cannot be null");
        if (reconcilerWorkerId == null || reconcilerWorkerId.isBlank()) {
            throw new IllegalArgumentException("reconcilerWorkerId must not be blank");
        }
        this.reconcilerWorkerId = reconcilerWorkerId;
        this.leaseDuration = Objects.requireNonNull(leaseDuration, "leaseDuration cannot be null");
        if (leaseDuration.isZero() || leaseDuration.isNegative()) {
            throw new IllegalArgumentException("leaseDuration must be positive");
        }
    }

    @Override
    public RecoveryReconciliationResult reconcile(String executionId) {
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId must not be blank");
        }

        Optional<ExecutionDurableState> initial = stateStore.load(executionId);
        if (initial.isEmpty()) {
            return result(executionId, RecoveryReconciliationAction.NO_OP, Optional.empty(),
                    "No durable execution state exists");
        }
        if (!isRecoveryInProgress(initial.get().status())) {
            return result(executionId, RecoveryReconciliationAction.NO_OP, Optional.empty(),
                    "Execution is not in a recoverable intermediate state");
        }
        if (leaseManager.getLease(executionId).isPresent()) {
            return result(executionId, RecoveryReconciliationAction.ACTIVE_RECOVERY, Optional.empty(),
                    "A recovery lease is still active; reconciliation was deferred");
        }

        final ExecutionLease lease;
        try {
            lease = leaseManager.acquireLease(executionId, reconcilerWorkerId, leaseDuration);
        } catch (RuntimeException unavailable) {
            return result(executionId, RecoveryReconciliationAction.LEASE_UNAVAILABLE, Optional.empty(),
                    "Could not acquire a fresh fencing lease; retry reconciliation later");
        }

        Optional<AttemptId> reconciledAttemptId = Optional.empty();
        try {
            ExecutionDurableState current = stateStore.load(executionId).orElse(null);
            if (current == null || !isRecoveryInProgress(current.status())) {
                return result(executionId, RecoveryReconciliationAction.NO_OP, Optional.empty(),
                        "Execution state changed before reconciliation acquired ownership");
            }

            Optional<ExecutionAttempt> latest = attemptStore.getLatest(executionId);
            if (latest.isPresent()) {
                ExecutionAttempt attempt = latest.get();
                reconciledAttemptId = Optional.of(attempt.attemptId());

                if (current.status() == ExecutionStatus.RESUMING && attempt.state() == AttemptState.RUNNING) {
                    stateStore.save(current.withStatus(ExecutionStatus.RUNNING), lease.fencingToken());
                    return result(executionId, RecoveryReconciliationAction.PROMOTED_RUNNING,
                            reconciledAttemptId,
                            "Restoration had completed; promoted execution after the attempt RUNNING write");
                }

                if (isInFlight(attempt.state())) {
                    attemptStore.updateState(attempt.attemptId(), AttemptState.LOST, lease.fencingToken());
                }
            }

            // This also repairs the crash window after RECOVERING was persisted but
            // before the attempt record was created.
            ExecutionDurableState latestState = stateStore.load(executionId).orElseThrow();
            if (isRecoveryInProgress(latestState.status())) {
                stateStore.save(latestState.withStatus(ExecutionStatus.SUSPENDED), lease.fencingToken());
            }
            return result(executionId, RecoveryReconciliationAction.MARKED_SUSPENDED,
                    reconciledAttemptId,
                    latest.isPresent()
                            ? "Interrupted recovery attempt fenced as LOST; execution returned to SUSPENDED"
                            : "No recovery attempt was persisted; execution returned to SUSPENDED");
        } finally {
            try {
                leaseManager.releaseLease(executionId, reconcilerWorkerId);
            } catch (RuntimeException ignored) {
                // Expiry is the fallback if the adapter cannot release the lease.
            }
        }
    }

    private static boolean isRecoveryInProgress(ExecutionStatus status) {
        return status == ExecutionStatus.RECOVERING || status == ExecutionStatus.RESUMING;
    }

    private static boolean isInFlight(AttemptState state) {
        return state == AttemptState.INITIALIZING
                || state == AttemptState.STARTING
                || state == AttemptState.RUNNING
                || state == AttemptState.CHECKPOINTING
                || state == AttemptState.PAUSED;
    }

    private static RecoveryReconciliationResult result(String executionId,
            RecoveryReconciliationAction action, Optional<AttemptId> attemptId, String detail) {
        return new RecoveryReconciliationResult(executionId, action, attemptId, detail);
    }
}
