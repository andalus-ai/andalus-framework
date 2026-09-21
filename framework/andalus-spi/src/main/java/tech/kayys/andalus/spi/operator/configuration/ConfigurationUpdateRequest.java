package tech.kayys.andalus.spi.operator.configuration;

import java.util.Map;

/**
 * Request to update configuration properties.
 */
public record ConfigurationUpdateRequest(
        Map<String, Object> values,
        boolean activate
) {
    public ConfigurationUpdateRequest {
        values = values != null ? Map.copyOf(values) : Map.of();
    }
}
