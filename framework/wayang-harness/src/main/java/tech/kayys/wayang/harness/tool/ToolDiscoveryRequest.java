package tech.kayys.wayang.harness.tool;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.resource.ResourceScope;

import java.util.Set;

/**
 * Represents a tool discovery request.
 *
 * <p>Its components capture `capabilities`, `identity`, `resources`.</p>
 *
 * @param capabilities the capabilities
 * @param identity the identity
 * @param resources the resources
 */


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
