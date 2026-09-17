package tech.kayys.wayang.execution.governance;

import tech.kayys.wayang.tool.ToolDescriptor;

import java.util.List;

/**
 * Extension of {@link ToolDescriptor} that declares the governance permissions required to execute the tool.
 */
public interface GovernedToolDescriptor extends ToolDescriptor {

    /**
     * Permissions required to execute this tool.
     *
     * <p>Default is empty for backward compatibility with existing tools.</p>
     */
    default List<ToolPermissionRequirement> permissions() {
        return List.of();
    }
}
