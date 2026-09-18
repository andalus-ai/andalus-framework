package tech.kayys.wayang.spi.sandbox;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public record ExecutionIsolationContext(
        String executionId,
        Optional<String> tenantId,
        Optional<String> agentId,
        Optional<String> userId,
        Optional<String> correlationId,
        ExecutionIsolationProfile profile,
        Instant deadline,
        Map<String, Object> attributes
) {

    public ExecutionIsolationContext {
        tenantId = tenantId == null ? Optional.empty() : tenantId;
        agentId = agentId == null ? Optional.empty() : agentId;
        userId = userId == null ? Optional.empty() : userId;
        correlationId = correlationId == null
                ? Optional.empty()
                : correlationId;

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
