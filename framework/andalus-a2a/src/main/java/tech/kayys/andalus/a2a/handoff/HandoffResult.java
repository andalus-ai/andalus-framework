package tech.kayys.andalus.a2a.handoff;

import java.time.Instant;
import java.util.Map;

public record HandoffResult(
        String handoffId,
        boolean accepted,
        String targetAgentId,
        String assignedTaskId,
        String reason,
        Instant completedAt,
        Map<String, Object> metadata
) {
    public HandoffResult {
        completedAt = completedAt == null ? Instant.now() : completedAt;
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static HandoffResult accepted(String handoffId, String targetAgentId, String taskId) {
        return new HandoffResult(handoffId, true, targetAgentId, taskId, "", Instant.now(), Map.of());
    }

    public static HandoffResult rejected(String handoffId, String targetAgentId, String reason) {
        return new HandoffResult(handoffId, false, targetAgentId, null, reason, Instant.now(), Map.of());
    }
}
