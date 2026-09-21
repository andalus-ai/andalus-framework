package tech.kayys.andalus.harness.protocol;

import tech.kayys.andalus.tool.ToolDescriptor;
import tech.kayys.andalus.tool.ToolId;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Defines the contract for tool catalog view operations in the Andalus framework.
 */


public interface ToolCatalogView {

    Collection<ToolDescriptor> availableTools();

    Set<String> availableCapabilities();

    Optional<ToolDescriptor> find(ToolId toolId);
}
