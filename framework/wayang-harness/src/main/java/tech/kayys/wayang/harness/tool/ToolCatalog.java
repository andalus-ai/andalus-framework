package tech.kayys.wayang.harness.tool;

import java.util.Collection;
import java.util.Optional;

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
