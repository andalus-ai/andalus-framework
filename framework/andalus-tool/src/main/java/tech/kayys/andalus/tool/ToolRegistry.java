package tech.kayys.andalus.tool;

import tech.kayys.andalus.registry.Registry;
import java.util.Optional;
import java.util.List;

import tech.kayys.andalus.tool.capability.CapabilityRequest;

public interface ToolRegistry extends Registry<Tool> {
    Optional<Tool> findByName(String name);
    List<Tool> listTools();
    
    /**
     * Queries the registry for tools that satisfy the given capability request.
     */
    List<Tool> getToolsByCapability(CapabilityRequest request);

    /**
     * Registers a tool provider dynamically.
     */
    void registerProvider(ToolProvider provider);
}
