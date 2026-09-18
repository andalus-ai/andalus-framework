package tech.kayys.wayang.harness.artifact;

import java.util.Objects;
import java.util.UUID;

/**
 * Unique identifier for an immutable artifact.
 */
public record ArtifactId(String value) {
    public ArtifactId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) {
            throw new IllegalArgumentException("ArtifactId cannot be blank");
        }
    }

    public static ArtifactId of(String value) {
        return new ArtifactId(value);
    }

    public static ArtifactId generate() {
        return new ArtifactId("art-" + UUID.randomUUID());
    }
}
