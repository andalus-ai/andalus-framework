package tech.kayys.wayang.state.retention;

import java.time.Instant;
import java.util.Objects;

public record StateOrArtifact(
        String id,
        String type,
        Instant createdAt,
        Instant lastAccessedAt,
        long sizeBytes
) {
    public StateOrArtifact {
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
    }
}
