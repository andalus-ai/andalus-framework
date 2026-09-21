package tech.kayys.andalus.spi.operator.sandbox;

import tech.kayys.andalus.spi.sandbox.observability.SandboxHealthStatus;

import java.time.Instant;
import java.util.Map;

public record SandboxHealthSummary(
        String sandboxId,
        SandboxHealthStatus status,
        Instant checkedAt,
        String reason,
        Map<String, Object> attributes
) {

    public SandboxHealthSummary {
        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
