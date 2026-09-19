package tech.kayys.wayang.execution.recovery;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record RecoveryPlan(
        String executionId,
        RecoveryMode mode,
        Optional<String> checkpointId,
        Map<String, Object> requiredResources,
        Map<String, Object> coordinationRequirements,
        Instant plannedAt
) {
    public RecoveryPlan {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(mode, "mode cannot be null");
        requiredResources = requiredResources == null ? Map.of() : Map.copyOf(requiredResources);
        coordinationRequirements = coordinationRequirements == null ? Map.of() : Map.copyOf(coordinationRequirements);
    }
}
