package tech.kayys.wayang.spi.sandbox.artifact;

import java.util.Map;

public record ArtifactPolicyContext(
        String artifactId,
        String executionId,
        String tenantId,
        String sandboxId,
        ArtifactDirection direction,
        ArtifactLocation location,
        long size,
        String mediaType,
        Map<String, Object> attributes
) {
    public ArtifactPolicyContext {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
