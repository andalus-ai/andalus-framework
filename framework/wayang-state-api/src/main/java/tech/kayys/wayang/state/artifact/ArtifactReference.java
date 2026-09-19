package tech.kayys.wayang.state.artifact;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;

/**
 * Lightweight reference to an artifact without needing to load its contents.
 */
public record ArtifactReference(
        ArtifactId id,
        String name,
        ArtifactType type,
        ArtifactDigest digest,
        ArtifactSize size,
        ArtifactLocation location,
        Instant createdAt,
        Map<String, Object> metadata
) {
    public ArtifactReference {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(digest, "digest cannot be null");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ArtifactReference of(ArtifactId id, String name, ArtifactType type, ArtifactDigest digest, ArtifactSize size, ArtifactLocation location) {
        return new ArtifactReference(id, name, type, digest, size, location, Instant.now(), Map.of());
    }
}
