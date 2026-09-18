package tech.kayys.wayang.harness.artifact;

import java.util.Objects;

/**
 * Default record implementation of {@link Artifact}.
 */
public record DefaultArtifact(
        ArtifactId id,
        ArtifactType type,
        ArtifactDescriptor descriptor,
        ArtifactDigest digest,
        ArtifactMetadata metadata,
        ArtifactProvenance provenance
) implements Artifact {
    public DefaultArtifact {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(descriptor, "descriptor");
        Objects.requireNonNull(digest, "digest");
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(provenance, "provenance");
    }
}
