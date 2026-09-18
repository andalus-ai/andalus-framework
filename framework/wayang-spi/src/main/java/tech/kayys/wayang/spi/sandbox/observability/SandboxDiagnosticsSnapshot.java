package tech.kayys.wayang.spi.sandbox.observability;

import tech.kayys.wayang.spi.sandbox.SandboxState;
import tech.kayys.wayang.spi.sandbox.SandboxType;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

public record SandboxDiagnosticsSnapshot(
        String sandboxId,
        String executionId,
        SandboxType type,
        SandboxState state,
        SandboxHealthStatus health,
        Set<SandboxObservabilityFeature> supportedFeatures,
        ResourceMetrics resources,
        Instant timestamp,
        Map<String, Object> attributes
) {
    public SandboxDiagnosticsSnapshot {
        if (sandboxId == null || sandboxId.isBlank()) {
            throw new IllegalArgumentException("sandboxId must not be blank");
        }
        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException("executionId must not be blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }
        if (state == null) {
            throw new IllegalArgumentException("state must not be null");
        }
        if (health == null) {
            throw new IllegalArgumentException("health must not be null");
        }
        if (timestamp == null) {
            throw new IllegalArgumentException("timestamp must not be null");
        }
        supportedFeatures = supportedFeatures == null ? Set.of() : Set.copyOf(supportedFeatures);
        resources = resources == null ? ResourceMetrics.empty() : resources;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
