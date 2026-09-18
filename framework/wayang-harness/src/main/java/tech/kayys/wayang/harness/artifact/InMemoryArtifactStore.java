package tech.kayys.wayang.harness.artifact;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory thread-safe content-addressable implementation of {@link ArtifactStore}.
 */
public class InMemoryArtifactStore implements ArtifactStore {

    private final Map<ArtifactId, Artifact> artifacts = new ConcurrentHashMap<>();
    private final Map<ArtifactId, ArtifactContent> contents = new ConcurrentHashMap<>();
    private final Map<ArtifactId, ArtifactRetention> retentions = new ConcurrentHashMap<>();

    @Override
    public Artifact put(ArtifactContent content, ArtifactMetadata metadata, ArtifactProvenance provenance) {
        Objects.requireNonNull(content, "content");
        Objects.requireNonNull(metadata, "metadata");
        Objects.requireNonNull(provenance, "provenance");

        ArtifactId id = ArtifactId.generate();
        ArtifactDigest digest = ArtifactDigest.sha256(content.bytes());
        ArtifactDescriptor descriptor = new ArtifactDescriptor(
                id,
                metadata.name(),
                ArtifactType.CUSTOM,
                digest,
                ArtifactRetention.TEMPORARY,
                Map.of()
        );

        DefaultArtifact artifact = new DefaultArtifact(id, ArtifactType.CUSTOM, descriptor, digest, metadata, provenance);
        artifacts.put(id, artifact);
        contents.put(id, content);
        retentions.put(id, ArtifactRetention.TEMPORARY);
        return artifact;
    }

    @Override
    public Optional<ArtifactContent> get(ArtifactId id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(contents.get(id));
    }

    @Override
    public Optional<Artifact> describe(ArtifactId id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(artifacts.get(id));
    }

    @Override
    public void retain(ArtifactId id) {
        if (id != null && artifacts.containsKey(id)) {
            retentions.put(id, ArtifactRetention.RETAINED);
        }
    }

    @Override
    public void release(ArtifactId id) {
        if (id != null) {
            ArtifactRetention retention = retentions.get(id);
            if (retention != ArtifactRetention.IMMUTABLE && retention != ArtifactRetention.RETAINED) {
                artifacts.remove(id);
                contents.remove(id);
                retentions.remove(id);
            }
        }
    }
}
