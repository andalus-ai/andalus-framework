package tech.kayys.wayang.spi.operator.sandbox;

import tech.kayys.wayang.spi.sandbox.SandboxState;
import tech.kayys.wayang.spi.sandbox.SandboxType;

import java.time.Instant;
import java.util.Map;

public record SandboxSummary(
        String sandboxId,
        String executionId,
        String tenantId,
        String agentId,
        SandboxType type,
        SandboxState state,
        String providerId,
        Instant createdAt,
        Instant startedAt,
        Map<String, Object> attributes
) {

    public SandboxSummary {
        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
