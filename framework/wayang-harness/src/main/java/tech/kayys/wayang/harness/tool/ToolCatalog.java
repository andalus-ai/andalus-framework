package tech.kayys.wayang.harness.tool;

import java.util.Collection;
import java.util.Optional;

/**
 * Defines the contract for tool catalog operations in the Wayang framework.
 */


public interface ToolCatalog {

    Collection<ToolDescriptor> discover(ToolDiscoveryRequest request);

    void register(ToolProvider provider);

    void unregister(ToolProvider provider);

    Optional<ToolProvider> providerFor(ToolId toolId);

    Optional<ToolDescriptor> get(ToolId toolId);

    static ToolCatalog create() {
        return new DefaultToolCatalog();
    }
}
