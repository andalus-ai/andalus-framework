package tech.kayys.wayang.execution.core.recovery;

import tech.kayys.wayang.execution.attempt.AttemptId;
import tech.kayys.wayang.execution.attempt.AttemptState;
import tech.kayys.wayang.execution.attempt.ExecutionAttempt;
import tech.kayys.wayang.execution.checkpoint.CheckpointStore;
import tech.kayys.wayang.execution.checkpoint.ExecutionCheckpoint;
import tech.kayys.wayang.execution.recovery.*;

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
        Optional<ExecutionCheckpoint> latestCheckpoint = checkpointStore.getLatest(executionId);
        if (latestCheckpoint.isPresent()) {
            return new RecoveryAssessment(
                    executionId,
                    true,
                    RecoveryMode.RESUME,
                    latestCheckpoint,
                    "Checkpoint " + latestCheckpoint.get().checkpointId() + " available for resumption"
            );
        } else {
            return new RecoveryAssessment(
                    executionId,
                    true,
                    RecoveryMode.RESTART,
                    Optional.empty(),
                    "No checkpoint found; clean restart recommended"
            );
        }
    }

    @Override
    public RecoveryPlan plan(String executionId, RecoveryAssessment assessment) {
        Objects.requireNonNull(assessment, "assessment cannot be null");
        Optional<String> cpId = assessment.latestCheckpoint().map(ExecutionCheckpoint::checkpointId);

        return new RecoveryPlan(
                executionId,
                assessment.recommendedMode(),
                cpId,
                Map.of("workspaceLease", "required"),
                Map.of("fencingToken", "required"),
                Instant.now()
        );
    }

    @Override
    public RecoveryResult recover(RecoveryPlan plan) {
        Objects.requireNonNull(plan, "plan cannot be null");
        if (plan.mode() == RecoveryMode.ABORT) {
            return RecoveryResult.failure(plan.executionId(), plan.mode(), "Recovery aborted by plan");
        }

        String newAttemptId = AttemptId.random().value();
        return RecoveryResult.success(plan.executionId(), plan.mode(), newAttemptId);
    }
}
