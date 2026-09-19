package tech.kayys.wayang.state.artifact;

import java.util.Map;
import java.util.Objects;

public record ArtifactInput(
        String name,
        ArtifactType type,
        byte[] content,
        String mimeType,
        Map<String, Object> metadata
) {
    public ArtifactInput {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
        Objects.requireNonNull(content, "content cannot be null");
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public static ArtifactInput of(String name, ArtifactType type, byte[] content) {
        return new ArtifactInput(name, type, content, "application/octet-stream", Map.of());
    }
}
