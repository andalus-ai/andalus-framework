package tech.kayys.andalus.harness.tool;

import tech.kayys.andalus.harness.memory.MemoryStore;
import tech.kayys.andalus.tool.ToolExecutionContext;
import tech.kayys.andalus.tool.ToolResult;
import tech.kayys.andalus.tool.event.ToolEvent;
import tech.kayys.andalus.tool.resolution.ToolIntent;

import java.util.function.Consumer;

/**
 * Defines the contract for tool result router operations in the Andalus framework.
 */
public interface ToolResultRouter {

    void route(
            ToolResult result,
            ToolIntent intent,
            ToolExecutionContext context,
            MemoryStore memoryStore,
            Consumer<ToolEvent> eventConsumer
    );

    static ToolResultRouter standard() {
        return new DefaultToolResultRouter();
    }
}
