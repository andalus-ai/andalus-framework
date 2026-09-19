package tech.kayys.wayang.spi.operator.tool;

import java.util.List;
import java.util.Map;

public record ToolSummary(
        String name,
        String description,
        String version,
        String providerId,
        List<ToolPermissionRequirement> permissions,
        List<String> capabilities,
        Map<String, Object> attributes) {

    public ToolSummary {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name must not be blank");
        }

        permissions = permissions == null
                ? List.of()
                : List.copyOf(permissions);

        capabilities = capabilities == null
                ? List.of()
                : List.copyOf(capabilities);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
