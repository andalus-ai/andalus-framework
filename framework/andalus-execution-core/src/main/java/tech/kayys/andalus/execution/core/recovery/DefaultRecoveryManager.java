package tech.kayys.andalus.execution.core.recovery;

import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.checkpoint.CheckpointStore;
import tech.kayys.andalus.execution.checkpoint.ExecutionCheckpoint;
import tech.kayys.andalus.execution.recovery.*;
import tech.kayys.andalus.execution.mobility.ExecutionDurableState;
import tech.kayys.andalus.execution.mobility.ExecutionLease;
import tech.kayys.andalus.execution.mobility.ExecutionStatus;
import tech.kayys.andalus.execution.mobility.LeaseManager;
import tech.kayys.andalus.execution.mobility.state.ExecutionStateStore;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class DefaultRecoveryManager implements RecoveryManager {

    private final CheckpointStore checkpointStore;
    private final LeaseManager leaseManager;
    private final ExecutionStateStore stateStore;
    private final String recoveryWorkerId;
    private final Duration leaseDuration;

    public DefaultRecoveryManager(CheckpointStore checkpointStore) {
        this.checkpointStore = Objects.requireNonNull(checkpointStore, "checkpointStore cannot be null");
        this.leaseManager = null;
        this.stateStore = null;
        this.recoveryWorkerId = null;
        this.leaseDuration = null;
    }

    /**
     * Creates a recovery manager that claims a fenced execution lease and persists RECOVERING
     * before reporting that recovery ownership has been established.
     */
    public DefaultRecoveryManager(CheckpointStore checkpointStore, LeaseManager leaseManager,
            ExecutionStateStore stateStore, String recoveryWorkerId, Duration leaseDuration) {
        this.checkpointStore = Objects.requireNonNull(checkpointStore, "checkpointStore cannot be null");
        this.leaseManager = Objects.requireNonNull(leaseManager, "leaseManager cannot be null");
        this.stateStore = Objects.requireNonNull(stateStore, "stateStore cannot be null");
        if (recoveryWorkerId == null || recoveryWorkerId.isBlank()) {
            throw new IllegalArgumentException("recoveryWorkerId must not be blank");
        }
        this.recoveryWorkerId = recoveryWorkerId;
        this.leaseDuration = Objects.requireNonNull(leaseDuration, "leaseDuration cannot be null");
        if (leaseDuration.isZero() || leaseDuration.isNegative()) {
            throw new IllegalArgumentException("leaseDuration must be positive");
        }
    }

    @Override
    public RecoveryAssessment assess(String executionId) {
        requireExecutionId(executionId);
        Optional<ExecutionCheckpoint> latestCheckpoint = checkpointStore.getLatest(executionId);
        if (latestCheckpoint.isPresent()) {
            ExecutionCheckpoint checkpoint = latestCheckpoint.get();
            if (!executionId.equals(checkpoint.executionId())) {
                return new RecoveryAssessment(executionId, false, RecoveryMode.ABORT, Optional.empty(),
                        "Checkpoint store returned a checkpoint for a different execution");
            }
            return new RecoveryAssessment(executionId, true, RecoveryMode.RESUME, latestCheckpoint,
                    "Checkpoint " + checkpoint.checkpointId() + " available for resumption");
        }
        return new RecoveryAssessment(executionId, true, RecoveryMode.RESTART, Optional.empty(),
                "No checkpoint found; clean restart recommended");
    }

    @Override
    public RecoveryPlan plan(String executionId, RecoveryAssessment assessment) {
        requireExecutionId(executionId);
        Objects.requireNonNull(assessment, "assessment cannot be null");
        if (!executionId.equals(assessment.executionId())) {
            throw new IllegalArgumentException("assessment executionId does not match requested execution");
        }
        if (!assessment.recoverable() || assessment.recommendedMode() == RecoveryMode.ABORT) {
            return new RecoveryPlan(executionId, RecoveryMode.ABORT, Optional.empty(), Map.of(), Map.of(),
                    Instant.now());
        }

        Optional<ExecutionCheckpoint> assessedCheckpoint = assessment.latestCheckpoint();
        Optional<ExecutionCheckpoint> currentCheckpoint = checkpointStore.getLatest(executionId);
        if (!sameCheckpoint(assessedCheckpoint, currentCheckpoint)) {
            throw new IllegalStateException("Recovery assessment is stale; reassess before planning");
        }
        if (assessment.recommendedMode() == RecoveryMode.RESUME && assessedCheckpoint.isEmpty()) {
            throw new IllegalArgumentException("RESUME recovery requires a checkpoint");
        }
        if (assessment.recommendedMode() == RecoveryMode.RESTART && assessedCheckpoint.isPresent()) {
            throw new IllegalArgumentException("RESTART recovery cannot discard an available checkpoint");
        }

        Optional<String> checkpointId = assessedCheckpoint.map(ExecutionCheckpoint::checkpointId);
        return new RecoveryPlan(executionId, assessment.recommendedMode(), checkpointId,
                Map.of("workspaceLease", "required"), Map.of("fencingToken", "required"), Instant.now());
    }

    @Override
    public RecoveryResult recover(RecoveryPlan plan) {
        Objects.requireNonNull(plan, "plan cannot be null");
        if (plan.mode() == RecoveryMode.ABORT) {
            return RecoveryResult.failure(plan.executionId(), plan.mode(), "Recovery aborted by plan");
        }

        Optional<ExecutionCheckpoint> currentCheckpoint = checkpointStore.getLatest(plan.executionId());
        if (plan.mode() == RecoveryMode.RESUME) {
            if (plan.checkpointId().isEmpty()) {
                return RecoveryResult.failure(plan.executionId(), plan.mode(),
                        "RESUME plan does not identify a checkpoint");
            }
            if (currentCheckpoint.isEmpty()
                    || !plan.checkpointId().get().equals(currentCheckpoint.get().checkpointId())
                    || !plan.executionId().equals(currentCheckpoint.get().executionId())) {
                return RecoveryResult.failure(plan.executionId(), plan.mode(),
                        "Recovery plan checkpoint is missing or stale; reassess before recovery");
            }
        } else if (plan.mode() == RecoveryMode.RESTART && currentCheckpoint.isPresent()) {
            return RecoveryResult.failure(plan.executionId(), plan.mode(),
                    "A checkpoint now exists; refusing stale RESTART plan");
        } else if (plan.mode() != RecoveryMode.RESTART) {
            return RecoveryResult.failure(plan.executionId(), plan.mode(), "Unsupported recovery mode");
        }

        if (leaseManager == null) {
            // Compatibility mode for callers that only use the recovery planning contract.
            String newAttemptId = AttemptId.random().value();
            return RecoveryResult.success(plan.executionId(), plan.mode(), newAttemptId);
        }

        ExecutionDurableState currentState = stateStore.load(plan.executionId()).orElse(null);
        if (currentState == null) {
            return RecoveryResult.failure(plan.executionId(), plan.mode(),
                    "Cannot recover execution without durable state");
        }
        if (currentState.status().isTerminal()) {
            return RecoveryResult.failure(plan.executionId(), plan.mode(),
                    "Cannot recover a terminal execution");
        }

        ExecutionLease lease = null;
        try {
            lease = leaseManager.acquireLease(plan.executionId(), recoveryWorkerId, leaseDuration);
            ExecutionDurableState recovering = currentState.withStatus(ExecutionStatus.RECOVERING);
            stateStore.save(recovering, lease.fencingToken());
            String newAttemptId = AttemptId.random().value();
            return RecoveryResult.success(plan.executionId(), plan.mode(), newAttemptId);
        } catch (RuntimeException failure) {
            if (lease != null) {
                leaseManager.releaseLease(plan.executionId(), recoveryWorkerId);
            }
            return RecoveryResult.failure(plan.executionId(), plan.mode(),
                    "Failed to claim fenced recovery ownership: " + failure.getMessage());
        }
    }

    private static boolean sameCheckpoint(Optional<ExecutionCheckpoint> left,
            Optional<ExecutionCheckpoint> right) {
        return left.map(ExecutionCheckpoint::checkpointId).equals(right.map(ExecutionCheckpoint::checkpointId));
    }

    private static void requireExecutionId(String executionId) {
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId must not be blank");
        }
    }
}
