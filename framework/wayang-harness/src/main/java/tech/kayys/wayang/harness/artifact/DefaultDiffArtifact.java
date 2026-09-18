package tech.kayys.wayang.harness.artifact;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Default implementation of {@link DiffArtifact}.
 */
public record DefaultDiffArtifact(
        ArtifactId id,
        ArtifactType type,
        ArtifactDescriptor descriptor,
        ArtifactDigest digest,
        ArtifactMetadata metadata,
        ArtifactProvenance provenance,
        DiffFormat format,
        Collection<FileChange> changes
) implements DiffArtifact {
    public DefaultDiffArtifact {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(descriptor, "descriptor");
        Objects.requireNonNull(digest, "digest");
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(provenance, "provenance");
        format = format != null ? format : DiffFormat.UNIFIED;
        changes = changes != null ? List.copyOf(changes) : List.of();
    }
}
