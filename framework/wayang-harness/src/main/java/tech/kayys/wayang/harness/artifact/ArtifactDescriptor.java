package tech.kayys.wayang.harness.artifact;

import java.util.Map;
import java.util.Objects;

/**
 * High-level descriptor of an artifact in Wayang Harness v3.1.
 */
public record ArtifactDescriptor(
        ArtifactId id,
        String name,
        ArtifactType type,
        ArtifactDigest digest,
        ArtifactRetention retention,
        Map<String, String> labels
) {
    public ArtifactDescriptor {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(digest, "digest");
        retention = retention != null ? retention : ArtifactRetention.TEMPORARY;
        labels = labels != null ? Map.copyOf(labels) : Map.of();
    }
}
