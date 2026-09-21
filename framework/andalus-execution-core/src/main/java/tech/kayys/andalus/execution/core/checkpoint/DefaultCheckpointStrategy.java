package tech.kayys.andalus.execution.core.checkpoint;

import tech.kayys.andalus.execution.checkpoint.CheckpointDecision;
import tech.kayys.andalus.execution.checkpoint.CheckpointStrategy;

import java.util.Map;
import java.util.Set;

public class DefaultCheckpointStrategy implements CheckpointStrategy {

    private static final Set<String> CHECKPOINT_TRIGGERS = Set.of(
            "BEFORE_EXTERNAL_SIDE_EFFECT",
            "BEFORE_PAUSE",
            "BEFORE_PREEMPTION",
            "MILESTONE",
            "TASK_COMPLETED",
            "STATE_CHANGE"
    );

    @Override
    public CheckpointDecision shouldCheckpoint(String executionId, String triggerEventType, Map<String, Object> context) {
        if (CHECKPOINT_TRIGGERS.contains(triggerEventType)) {
            return CheckpointDecision.CHECKPOINT_NOW;
        }
        return CheckpointDecision.SKIP;
    }
}
