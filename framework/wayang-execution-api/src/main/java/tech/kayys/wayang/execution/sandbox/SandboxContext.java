package tech.kayys.wayang.execution.sandbox;

import java.util.Map;

/**
 * Execution context metadata for sandbox evaluation.
 */
public record SandboxContext(
        String tenantId,
        String executionId,
        String agentId,
        Map<String, Object> attributes
) {

    public SandboxContext {
        tenantId = tenantId != null ? tenantId.trim() : "default";
        executionId = executionId != null ? executionId.trim() : "default-exec";
        agentId = agentId != null ? agentId.trim() : "system";
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public static SandboxContext defaultContext() {
        return new SandboxContext("default", "exec-0", "agent-0", Map.of());
    }

    public static SandboxContext of(String tenantId, String executionId, String agentId) {
        return new SandboxContext(tenantId, executionId, agentId, Map.of());
    }
}
