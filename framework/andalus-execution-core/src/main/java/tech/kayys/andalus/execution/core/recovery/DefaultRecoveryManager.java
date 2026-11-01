package tech.kayys.andalus.execution.core.recovery;

import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.checkpoint.CheckpointStore;
import tech.kayys.andalus.execution.checkpoint.ExecutionCheckpoint;
import tech.kayys.andalus.execution.recovery.*;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class DefaultRecoveryManager implements RecoveryManager {

    private final CheckpointStore checkpointStore;

    public DefaultRecoveryManager(CheckpointStore checkpointStore) {
        this.checkpointStore = Objects.requireNonNull(checkpointStore, "checkpointStore cannot be null");
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

        // This establishes a fresh attempt identity only. Runtime restoration and lease/fencing
        // acquisition are performed by the execution adapter, not by this planning component.
        String newAttemptId = AttemptId.random().value();
        return RecoveryResult.success(plan.executionId(), plan.mode(), newAttemptId);
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
