package tech.kayys.wayang.state.artifact;

import java.util.List;
import java.util.Optional;

public interface ArtifactMetadataStore {
    void save(ArtifactReference reference);
    Optional<ArtifactReference> get(ArtifactId id);
    Optional<ArtifactReference> getByDigest(ArtifactDigest digest);
    List<ArtifactReference> findByType(ArtifactType type);
}
