package tech.kayys.wayang.spi.operator.sandbox;

import tech.kayys.wayang.spi.sandbox.observability.ArtifactMetrics;
import tech.kayys.wayang.spi.sandbox.observability.FilesystemMetrics;
import tech.kayys.wayang.spi.sandbox.observability.NetworkMetrics;
import tech.kayys.wayang.spi.sandbox.observability.ResourceMetrics;

import java.time.Instant;

public record SandboxMetricsSummary(
        String sandboxId,
        String executionId,
        String providerId,
        Instant timestamp,
        ResourceMetrics resource,
        NetworkMetrics network,
        FilesystemMetrics filesystem,
        ArtifactMetrics artifacts
) {
}
