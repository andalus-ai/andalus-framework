package tech.kayys.wayang.state.artifact;

import java.util.Objects;
import java.util.UUID;

public record ArtifactId(String value) {
    public ArtifactId {
        Objects.requireNonNull(value, "value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank");
        }
    }

    public static ArtifactId of(String value) {
        return new ArtifactId(value);
    }

    public static ArtifactId random() {
        return new ArtifactId(UUID.randomUUID().toString());
    }
}
