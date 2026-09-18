package tech.kayys.wayang.harness.artifact;

import java.util.Map;
import java.util.Objects;

/**
 * An individual file or entry within an artifact bundle.
 */
public record ArtifactEntry(
        String path,
        long sizeBytes,
        ArtifactDigest digest,
        Map<String, String> attributes
) {
    public ArtifactEntry {
        Objects.requireNonNull(path, "path");
        Objects.requireNonNull(digest, "digest");
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }
}
