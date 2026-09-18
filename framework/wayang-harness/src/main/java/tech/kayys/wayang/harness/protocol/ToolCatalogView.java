package tech.kayys.wayang.harness.protocol;

import tech.kayys.wayang.harness.tool.ToolDescriptor;
import tech.kayys.wayang.harness.tool.ToolId;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface ToolCatalogView {

    Collection<ToolDescriptor> availableTools();

    Set<String> availableCapabilities();

    Optional<ToolDescriptor> find(ToolId toolId);
}
