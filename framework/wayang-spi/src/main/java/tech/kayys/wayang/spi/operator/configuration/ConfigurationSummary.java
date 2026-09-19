package tech.kayys.wayang.spi.operator.configuration;

import java.time.Instant;
import java.util.Map;

/**
 * Safe, immutable projection of a Configuration resource for operators.
 */
public record ConfigurationSummary(
        String id,
        String name,
        String source,
        String type,
        String tenantId,
        String status,
        Map<String, Object> values,
        Instant lastModifiedAt
) {
    public ConfigurationSummary {
        values = values != null ? Map.copyOf(values) : Map.of();
        lastModifiedAt = lastModifiedAt != null ? lastModifiedAt : Instant.now();
    }
}
