package tech.kayys.wayang.tool;

import java.util.*;

/**
 * SPI for dynamically supplying tools to the ToolRegistry and ToolCatalog.
 */
public interface ToolProvider {

    /**
     * @return the list of tools supplied by this provider.
     */
    default List<Tool> getTools() {
        return Collections.emptyList();
    }

    default Optional<ToolDescriptor> describe(ToolId id) {
        if (id == null) {
            return Optional.empty();
        }
        for (ToolDescriptor desc : tools()) {
            if (id.equals(desc.toolId()) || id.value().equals(desc.name())) {
                return Optional.of(desc);
            }
        }
        return Optional.empty();
    }

    default Collection<ToolDescriptor> tools() {
        return getTools().stream()
                .map(Tool::descriptor)
                .filter(Objects::nonNull)
                .toList();
    }

    default ToolExecutor executor(ToolId id) {
        if (id == null) {
            return null;
        }
        for (Tool tool : getTools()) {
            if (tool.descriptor() != null && (id.equals(tool.descriptor().toolId()) || id.value().equals(tool.descriptor().name()))) {
                return (inv, ctx) -> tool.execute(inv, ctx);
            }
        }
        return null;
    }
}
