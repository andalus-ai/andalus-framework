package tech.kayys.andalus.a2a.handoff;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Context transferred during a multi-agent handoff.
 * Contains execution identity, conversation state, and memory snapshots.
 * Analogous to thread context passing in OpenAI multi-agent patterns.
 */
public record HandoffContext(
        String executionId,
        String traceId,
        String turnId,
        String sessionId,
        String tenantId,
        String originalQuery,
        String lastAgentOutput,
        List<HandoffMemorySnapshot> memorySnapshots,
        Map<String, Object> sharedState,
        Map<String, Object> attributes
) {
    public HandoffContext {
        Objects.requireNonNull(executionId, "executionId");
        memorySnapshots = memorySnapshots == null ? List.of() : List.copyOf(memorySnapshots);
        sharedState = sharedState == null ? Map.of() : Map.copyOf(sharedState);
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static HandoffContext minimal(
            String executionId, String sessionId, String originalQuery) {
        return new HandoffContext(executionId, null, null, sessionId, null,
                originalQuery, null, List.of(), Map.of(), Map.of());
    }

    /** Return a context with updated last agent output */
    public HandoffContext withLastOutput(String output) {
        return new HandoffContext(executionId, traceId, turnId, sessionId, tenantId,
                originalQuery, output, memorySnapshots, sharedState, attributes);
    }
}
