package tech.kayys.wayang.state.artifact;

import java.util.Objects;

public record ArtifactLocation(String uri, String storageBackend) {
    public ArtifactLocation {
        Objects.requireNonNull(uri, "uri cannot be null");
        storageBackend = storageBackend == null ? "in-memory" : storageBackend;
    }
}
