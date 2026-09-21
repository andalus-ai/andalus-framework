package tech.kayys.andalus.spi.operator.sandbox;

import tech.kayys.andalus.spi.sandbox.observability.ArtifactMetrics;
import tech.kayys.andalus.spi.sandbox.observability.FilesystemMetrics;
import tech.kayys.andalus.spi.sandbox.observability.NetworkMetrics;
import tech.kayys.andalus.spi.sandbox.observability.ResourceMetrics;

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
