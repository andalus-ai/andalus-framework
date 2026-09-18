package tech.kayys.wayang.spi.sandbox;

import java.time.Instant;
import java.util.Map;

public record SandboxSnapshot(
        String sandboxId,
        String executionId,
        String tenantId,
        String agentId,
        String providerId,
        SandboxType type,
        SandboxState state,
        Instant createdAt,
        Instant updatedAt,
        Map<String, Object> attributes
) {
    public SandboxSnapshot {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
