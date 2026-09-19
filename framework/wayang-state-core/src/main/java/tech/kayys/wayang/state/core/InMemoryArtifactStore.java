package tech.kayys.wayang.state.core;

import tech.kayys.wayang.state.artifact.*;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe content-addressed ArtifactStore performing automatic SHA-256 deduplication.
 */
public class InMemoryArtifactStore implements ArtifactStore {

    private final Map<String, byte[]> contentByDigest = new ConcurrentHashMap<>();
    private final Map<String, ArtifactReference> refByDigest = new ConcurrentHashMap<>();
    private final Map<ArtifactId, ArtifactReference> refById = new ConcurrentHashMap<>();

    @Override
    public ArtifactReference put(ArtifactInput input) {
        Objects.requireNonNull(input, "input cannot be null");
        ArtifactDigest digest = ArtifactDigest.sha256(input.content());

        // Deduplication
        ArtifactReference existing = refByDigest.get(digest.hexValue());
        if (existing != null) {
            return existing;
        }

        ArtifactId id = ArtifactId.random();
        ArtifactSize size = ArtifactSize.of(input.content().length);
        ArtifactLocation location = new ArtifactLocation(digest.uri(), "in-memory");

        ArtifactReference reference = new ArtifactReference(
                id,
                input.name(),
                input.type(),
                digest,
                size,
                location,
                java.time.Instant.now(),
                input.metadata()
        );

        contentByDigest.put(digest.hexValue(), input.content());
        refByDigest.put(digest.hexValue(), reference);
        refById.put(id, reference);
        return reference;
    }

    @Override
    public Optional<ArtifactContent> get(ArtifactReference reference) {
        Objects.requireNonNull(reference, "reference cannot be null");
        byte[] data = contentByDigest.get(reference.digest().hexValue());
        if (data == null) {
            return Optional.empty();
        }
        return Optional.of(new ArtifactContent(reference, data, "application/octet-stream"));
    }

    @Override
    public Optional<ArtifactReference> findByDigest(ArtifactDigest digest) {
        return Optional.ofNullable(refByDigest.get(digest.hexValue()));
    }

    @Override
    public boolean exists(ArtifactDigest digest) {
        return contentByDigest.containsKey(digest.hexValue());
    }
}
