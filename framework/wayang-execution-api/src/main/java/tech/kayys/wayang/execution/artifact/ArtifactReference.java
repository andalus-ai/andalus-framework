package tech.kayys.wayang.execution.artifact;

import java.util.Objects;

/**
 * First-class reference to an artifact exported from or imported into a sandbox.
 */
public record ArtifactReference(
        String artifactId,
        String name,
        String uri,
        long sizeBytes,
        String sha256
) {

    public ArtifactReference {
        Objects.requireNonNull(artifactId, "artifactId cannot be null");
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(uri, "uri cannot be null");
        sha256 = sha256 != null ? sha256 : "";
    }
}
