package tech.kayys.wayang.harness.tool;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.resource.ResourceScope;

import java.util.Set;

public record ToolDiscoveryRequest(
        Set<String> capabilities,
        HarnessIdentity identity,
        ResourceScope resources
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
