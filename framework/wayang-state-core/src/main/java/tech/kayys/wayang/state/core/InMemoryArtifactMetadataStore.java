package tech.kayys.wayang.state.core;

import tech.kayys.wayang.state.artifact.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryArtifactMetadataStore implements ArtifactMetadataStore {

    private final Map<ArtifactId, ArtifactReference> byId = new ConcurrentHashMap<>();
    private final Map<String, ArtifactReference> byDigest = new ConcurrentHashMap<>();

    @Override
    public void save(ArtifactReference reference) {
        byId.put(reference.id(), reference);
        byDigest.put(reference.digest().hexValue(), reference);
    }

    @Override
    public Optional<ArtifactReference> get(ArtifactId id) {
        return Optional.ofNullable(byId.get(id));
    }

    @Override
    public Optional<ArtifactReference> getByDigest(ArtifactDigest digest) {
        return Optional.ofNullable(byDigest.get(digest.hexValue()));
    }

    @Override
    public List<ArtifactReference> findByType(ArtifactType type) {
        return byId.values().stream()
                .filter(r -> r.type() == type)
                .toList();
    }
}
