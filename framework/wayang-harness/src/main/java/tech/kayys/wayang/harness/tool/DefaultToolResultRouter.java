package tech.kayys.wayang.harness.tool;

import tech.kayys.wayang.harness.memory.*;

import java.util.Map;
import java.util.function.Consumer;

public class DefaultToolResultRouter implements ToolResultRouter {

    @Override
    public void route(
            ToolResult result,
            ToolIntent intent,
            ToolExecutionContext context,
            MemoryStore memoryStore,
            Consumer<ToolEvent> eventConsumer
    ) {
        if (result == null) {
            return;
        }

        // 1. Emit event
        if (eventConsumer != null) {
            ToolEventType eventType = switch (result.status()) {
                case SUCCESS -> ToolEventType.TOOL_COMPLETED;
                case CANCELED -> ToolEventType.TOOL_CANCELED;
                default -> ToolEventType.TOOL_FAILED;
            };
            eventConsumer.accept(ToolEvent.of(
                    result.invocationId(),
                    eventType,
                    Map.of(
                            "status", result.status().name(),
                            "durationMs", result.metadata().duration().toMillis(),
                            "preview", result.output() != null ? result.output().preview() : ""
                    )
            ));
        }

        // 2. Memory retention if requested by intent context
        if (memoryStore != null && result.isSuccess() && intent != null && intent.context() != null) {
            Object retain = intent.context().metadata().get("retainInMemory");
            if (Boolean.TRUE.equals(retain)) {
                String content = result.output().preview();
                memoryStore.store(new MemoryEntry(
                        MemoryId.generate(),
                        MemoryType.EPISODIC,
                        content,
                        MemoryScope.SESSION,
                        MemoryMetadata.observed("tool", intent.capability(), 1.0)
                ));
            }
        }
    }
}
