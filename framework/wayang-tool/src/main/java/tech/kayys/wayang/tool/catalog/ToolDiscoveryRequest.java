package tech.kayys.wayang.tool.catalog;

import java.util.Set;

public record ToolDiscoveryRequest(
        Set<String> capabilities,
        Object identity,
        Object resources
) {
    public ToolDiscoveryRequest {
        capabilities = capabilities != null ? Set.copyOf(capabilities) : Set.of();
    }

    public static ToolDiscoveryRequest forCapability(String capability) {
        return new ToolDiscoveryRequest(Set.of(capability), null, null);
    }

    public static ToolDiscoveryRequest all() {
        return new ToolDiscoveryRequest(Set.of(), null, null);
    }
}
