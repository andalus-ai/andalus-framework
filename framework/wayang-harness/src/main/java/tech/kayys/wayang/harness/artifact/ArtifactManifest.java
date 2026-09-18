package tech.kayys.wayang.harness.artifact;

import java.util.Collection;
import java.util.List;

/**
 * Manifest listing the entries of a composite or multi-file artifact.
 */
public interface ArtifactManifest {

    Collection<ArtifactEntry> entries();

    static ArtifactManifest of(Collection<ArtifactEntry> entries) {
        List<ArtifactEntry> copy = entries != null ? List.copyOf(entries) : List.of();
        return () -> copy;
    }
}
