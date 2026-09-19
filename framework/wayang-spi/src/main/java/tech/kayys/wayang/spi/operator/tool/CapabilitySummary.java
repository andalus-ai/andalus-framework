package tech.kayys.wayang.spi.operator.tool;

import tech.kayys.wayang.spi.capability.CapabilityType;

import java.util.List;
import java.util.Map;

public record CapabilitySummary(
        String id,
        CapabilityType type,
        String name,
        String description,
        String version,
        String providerId,
        boolean available,
        boolean healthy,
        List<String> tags,
        Map<String, Object> attributes) {

    public CapabilitySummary {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("id must not be blank");
        }

        tags = tags == null
                ? List.of()
                : List.copyOf(tags);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
