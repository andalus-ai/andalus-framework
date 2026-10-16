package tech.kayys.andalus.harness.memory;

import java.util.Map;

/**
 * Execution-scoped identity context for memory read and write operations.
 */
public record MemoryContext(
        String tenantId,
        String userId,
        String agentId,
        String sessionId,
        String executionId,
        String workspaceId,
        String projectId,
        Map<String, Object> attributes
) {

    public MemoryContext {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static MemoryContext empty() {
        return new MemoryContext(null, null, null, null, null, null, null, Map.of());
    }

    public static MemoryContext forSession(String sessionId, String agentId) {
        return new MemoryContext(null, null, agentId, sessionId, null, null, null, Map.of());
    }

    public static MemoryContext forExecution(String executionId, String agentId, String sessionId) {
        return new MemoryContext(null, null, agentId, sessionId, executionId, null, null, Map.of());
    }

    public MemoryContext withAttribute(String key, Object value) {
        var copy = new java.util.HashMap<>(attributes);
        copy.put(key, value);
        return new MemoryContext(tenantId, userId, agentId, sessionId, executionId, workspaceId, projectId, copy);
    }
}
