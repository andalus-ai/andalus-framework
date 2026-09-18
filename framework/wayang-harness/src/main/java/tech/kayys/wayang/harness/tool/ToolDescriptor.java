package tech.kayys.wayang.harness.tool;

import java.util.Set;

/**
 * Defines the contract for tool descriptor operations in the Wayang framework.
 */


public interface ToolDescriptor {

    ToolId id();

    String name();

    String version();

    Set<String> capabilities();

    ToolInputSchema inputSchema();

    ToolOutputSchema outputSchema();

    ToolExecutionProfile executionProfile();
}
