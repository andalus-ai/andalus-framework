package tech.kayys.wayang.spi.sandbox.artifact;

import java.nio.file.Path;
import java.util.Map;

public record ArtifactTransferRequest(
        String artifactId,
        String sandboxId,
        ArtifactDirection direction,
        ArtifactLocation location,
        Path relativePath,
        long maxBytes,
        Map<String, Object> attributes
) {
    public ArtifactTransferRequest {
        if (artifactId == null || artifactId.isBlank()) throw new IllegalArgumentException("artifactId must not be blank");
        if (sandboxId == null || sandboxId.isBlank()) throw new IllegalArgumentException("sandboxId must not be blank");
        if (direction == null) throw new IllegalArgumentException("direction must not be null");
        if (location == null) throw new IllegalArgumentException("location must not be null");
        if (relativePath == null || relativePath.isAbsolute()) throw new IllegalArgumentException("relativePath must be relative");
        if (maxBytes == 0 || maxBytes < -1) throw new IllegalArgumentException("maxBytes must be -1 or > 0");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
