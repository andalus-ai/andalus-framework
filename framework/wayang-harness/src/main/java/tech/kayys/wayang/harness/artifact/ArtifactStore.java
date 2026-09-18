package tech.kayys.wayang.harness.artifact;

import java.util.Optional;

/**
 * Universal content-addressable storage contract for artifacts in Wayang Harness v3.1.
 */
public interface ArtifactStore {

    Artifact put(ArtifactContent content, ArtifactMetadata metadata, ArtifactProvenance provenance);

    Optional<ArtifactContent> get(ArtifactId id);

    Optional<Artifact> describe(ArtifactId id);

    void retain(ArtifactId id);

    void release(ArtifactId id);
}
