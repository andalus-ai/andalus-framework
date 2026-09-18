package tech.kayys.wayang.spi.sandbox.artifact;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;

public record ArtifactUploadRequest(
        String sandboxId,
        String executionId,
        String tenantId,
        String artifactId,
        String name,
        String mediaType,
        ArtifactLocation location,
        Path relativePath,
        InputStream content,
        long maxBytes,
        Map<String, Object> attributes
) {
    public ArtifactUploadRequest {
        if (sandboxId == null || sandboxId.isBlank()) throw new IllegalArgumentException("sandboxId must not be blank");
        if (executionId == null || executionId.isBlank()) throw new IllegalArgumentException("executionId must not be blank");
        if (artifactId == null || artifactId.isBlank()) throw new IllegalArgumentException("artifactId must not be blank");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        if (location == null) throw new IllegalArgumentException("location must not be null");
        if (relativePath == null || relativePath.isAbsolute()) throw new IllegalArgumentException("relativePath must be relative");
        if (content == null) throw new IllegalArgumentException("content must not be null");
        if (maxBytes == 0 || maxBytes < -1) throw new IllegalArgumentException("maxBytes must be -1 or > 0");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
