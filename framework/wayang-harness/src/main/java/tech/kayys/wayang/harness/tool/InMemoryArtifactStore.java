package tech.kayys.wayang.harness.tool;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryArtifactStore implements ArtifactStore {

    private final Map<String, byte[]> store = new ConcurrentHashMap<>();

    @Override
    public String store(String name, byte[] data, String mimeType) {
        String id = "art-" + UUID.randomUUID();
        store.put(id, data != null ? data.clone() : new byte[0]);
        return id;
    }

    @Override
    public Optional<byte[]> retrieve(String artifactId) {
        byte[] data = store.get(artifactId);
        return data != null ? Optional.of(data.clone()) : Optional.empty();
    }
}
