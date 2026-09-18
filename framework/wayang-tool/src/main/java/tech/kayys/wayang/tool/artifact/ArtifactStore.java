package tech.kayys.wayang.tool.artifact;

import java.util.Optional;

/**
 * Contract for storing and retrieving tool artifact data.
 */
public interface ArtifactStore {

    String store(String name, byte[] data, String mimeType);

    Optional<byte[]> retrieve(String artifactId);

    static ArtifactStore inMemory() {
        return new InMemoryArtifactStore();
    }
}
