package tech.kayys.wayang.state.artifact;

import java.util.Objects;

public record ArtifactContent(
        ArtifactReference reference,
        byte[] data,
        String mimeType
) {
    public ArtifactContent {
        Objects.requireNonNull(reference, "reference cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
    }
}
