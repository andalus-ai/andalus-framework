package tech.kayys.andalus.execution.checkpoint;

import java.util.Map;

public interface CheckpointStrategy {
    CheckpointDecision shouldCheckpoint(String executionId, String triggerEventType, Map<String, Object> context);
}
