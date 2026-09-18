package tech.kayys.wayang.harness.tool;

import java.util.Collection;
import java.util.Optional;

/**
 * Defines the contract for tool provider operations in the Wayang framework.
 */


public interface ToolProvider {

    Optional<ToolDescriptor> describe(ToolId id);

    Collection<ToolDescriptor> tools();

    ToolExecutor executor(ToolId id);
}
