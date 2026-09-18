package tech.kayys.wayang.harness.tool;

import java.util.Set;

public interface ToolDescriptor {

    ToolId id();

    String name();

    String version();

    Set<String> capabilities();

    ToolInputSchema inputSchema();

    ToolOutputSchema outputSchema();

    ToolExecutionProfile executionProfile();
}
