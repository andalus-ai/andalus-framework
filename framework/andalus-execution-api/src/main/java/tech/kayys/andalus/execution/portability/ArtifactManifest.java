package tech.kayys.andalus.execution.portability;

import java.util.Map;
import java.util.Objects;

/**
 * Content-addressed index of artifacts bundled or referenced by the portable execution.
 */
public record ArtifactManifest(Map<String, String> artifactChecksums) {

    public ArtifactManifest {
        artifactChecksums = artifactChecksums != null ? Map.copyOf(artifactChecksums) : Map.of();
    }

    public static ArtifactManifest empty() {
        return new ArtifactManifest(Map.of());
    }

    public static ArtifactManifest of(Map<String, String> checksums) {
        return new ArtifactManifest(checksums);
    }
}
