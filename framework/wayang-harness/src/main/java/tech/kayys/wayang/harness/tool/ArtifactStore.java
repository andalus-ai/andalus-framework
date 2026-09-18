package tech.kayys.wayang.harness.tool;

import java.util.Optional;

/**
 * Defines the contract for artifact store operations in the Wayang framework.
 */


public interface ArtifactStore {

    String store(String name, byte[] data, String mimeType);

    Optional<byte[]> retrieve(String artifactId);

    static ArtifactStore inMemory() {
        return new InMemoryArtifactStore();
    }
}
