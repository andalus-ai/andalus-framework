package tech.kayys.wayang.execution.checkpoint;

import java.util.Map;

public interface CheckpointStrategy {
    CheckpointDecision shouldCheckpoint(String executionId, String triggerEventType, Map<String, Object> context);
}
