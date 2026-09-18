package tech.kayys.wayang.spi.sandbox.observability;

import java.time.Instant;
import java.util.Map;

public record SandboxMetricsSnapshot(
        String sandboxId,
        String executionId,
        String providerId,
        Instant timestamp,
        ResourceMetrics resources,
        NetworkMetrics network,
        FilesystemMetrics filesystem,
        ArtifactMetrics artifacts,
        Map<String, Object> attributes
) {

    public SandboxMetricsSnapshot {
        if (sandboxId == null || sandboxId.isBlank()) {
            throw new IllegalArgumentException(
                    "sandboxId must not be blank");
        }

        if (executionId == null || executionId.isBlank()) {
            throw new IllegalArgumentException(
                    "executionId must not be blank");
        }

        if (timestamp == null) {
            throw new IllegalArgumentException(
                    "timestamp must not be null");
        }

        resources = resources == null ? ResourceMetrics.empty() : resources;
        network = network == null ? NetworkMetrics.empty() : network;
        filesystem = filesystem == null ? FilesystemMetrics.empty() : filesystem;
        artifacts = artifacts == null ? ArtifactMetrics.empty() : artifacts;

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
