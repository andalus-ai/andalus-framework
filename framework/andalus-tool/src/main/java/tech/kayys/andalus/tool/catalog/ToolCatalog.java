package tech.kayys.andalus.tool.catalog;

import tech.kayys.andalus.tool.ToolDescriptor;
import tech.kayys.andalus.tool.ToolId;
import tech.kayys.andalus.tool.ToolProvider;

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
