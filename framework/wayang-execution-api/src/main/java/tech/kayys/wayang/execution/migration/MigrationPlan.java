package tech.kayys.wayang.execution.migration;

import java.time.Instant;
import java.util.Map;

public record MigrationPlan(
        String executionId,
        String sourceWorkerId,
        String targetWorkerId,
        String checkpointId,
        Instant plannedAt,
        Map<String, Object> parameters
) {}
