package tech.kayys.andalus.observability.analytics;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Common query boundary for requesting operational analytics.
 */
public record AnalyticsQuery(
        Instant from,
        Instant until,
        Optional<String> tenantId,
        Optional<String> workspaceId,
        Optional<String> projectId,
        Optional<String> agentId,
        Optional<String> executionId,
        Map<String, String> dimensions,
        int limit
) {

    public AnalyticsQuery {
        tenantId = tenantId == null ? Optional.empty() : tenantId;
        workspaceId = workspaceId == null ? Optional.empty() : workspaceId;
        projectId = projectId == null ? Optional.empty() : projectId;
        agentId = agentId == null ? Optional.empty() : agentId;
        executionId = executionId == null ? Optional.empty() : executionId;
        dimensions = dimensions == null ? Map.of() : Map.copyOf(dimensions);
        limit = limit <= 0 ? 100 : limit;
    }

    public static AnalyticsQuery forAgent(String agentId) {
        return new AnalyticsQuery(null, null, Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.ofNullable(agentId), Optional.empty(), Map.of(), 100);
    }

    public static AnalyticsQuery all() {
        return new AnalyticsQuery(null, null, Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Map.of(), 100);
    }
}
