package tech.kayys.wayang.harness.tool;

import tech.kayys.wayang.harness.memory.MemoryStore;
import tech.kayys.wayang.tool.ToolExecutionContext;
import tech.kayys.wayang.tool.ToolResult;
import tech.kayys.wayang.tool.event.ToolEvent;
import tech.kayys.wayang.tool.resolution.ToolIntent;

import java.util.function.Consumer;

/**
 * Defines the contract for tool result router operations in the Wayang framework.
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
