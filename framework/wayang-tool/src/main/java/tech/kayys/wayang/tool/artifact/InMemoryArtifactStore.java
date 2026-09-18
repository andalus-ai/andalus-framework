package tech.kayys.wayang.tool.artifact;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryArtifactStore implements ArtifactStore {

    private final Map<String, byte[]> storage = new ConcurrentHashMap<>();

    @Override
    public String store(String name, byte[] data, String mimeType) {
        String id = "art-" + UUID.randomUUID();
        storage.put(id, data != null ? data : new byte[0]);
        return id;
    }

    @Override
    public Optional<byte[]> retrieve(String artifactId) {
        return Optional.ofNullable(storage.get(artifactId));
    }
}
