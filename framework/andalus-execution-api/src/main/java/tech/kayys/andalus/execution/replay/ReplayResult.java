package tech.kayys.andalus.execution.replay;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Summary result produced by ReplayEngine.
 */
public record ReplayResult(
        String executionId,
        boolean success,
        ReplayMode modeUsed,
        Map<String, Object> summary,
        Instant completedAt
) {
    public ReplayResult {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(modeUsed, "modeUsed cannot be null");
        summary = summary == null ? Map.of() : Map.copyOf(summary);
        if (completedAt == null) {
            completedAt = Instant.now();
        }
    }

    public static ReplayResult success(String executionId, ReplayMode mode, Map<String, Object> summary) {
        return new ReplayResult(executionId, true, mode, summary, Instant.now());
    }

    public static ReplayResult failure(String executionId, ReplayMode mode, String error) {
        return new ReplayResult(executionId, false, mode, Map.of("error", error), Instant.now());
    }
}
