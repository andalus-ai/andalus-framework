package tech.kayys.wayang.harness.artifact;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Metadata descriptor for an artifact.
 */
public record ArtifactMetadata(
        String name,
        String mimeType,
        long sizeBytes,
        Instant createdAt,
        Map<String, String> properties
) {
    public ArtifactMetadata {
        Objects.requireNonNull(name, "name");
        mimeType = mimeType != null ? mimeType : "application/octet-stream";
        createdAt = createdAt != null ? createdAt : Instant.now();
        properties = properties != null ? Map.copyOf(properties) : Map.of();
    }

    public static ArtifactMetadata of(String name, String mimeType, long sizeBytes) {
        return new ArtifactMetadata(name, mimeType, sizeBytes, Instant.now(), Map.of());
    }

    public static ArtifactMetadata text(String name, long sizeBytes) {
        return new ArtifactMetadata(name, "text/plain", sizeBytes, Instant.now(), Map.of());
    }
}
