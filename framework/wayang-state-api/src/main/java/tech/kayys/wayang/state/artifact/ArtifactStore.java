package tech.kayys.wayang.state.artifact;

import java.util.Optional;

/**
 * Content-addressed store for artifacts.
 */
public interface ArtifactStore {
    ArtifactReference put(ArtifactInput input);
    Optional<ArtifactContent> get(ArtifactReference reference);
    Optional<ArtifactReference> findByDigest(ArtifactDigest digest);
    boolean exists(ArtifactDigest digest);
}
