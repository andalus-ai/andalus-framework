package tech.kayys.wayang.execution.checkpoint;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public record CheckpointManifest(
        String checkpointId,
        long sequence,
        List<String> stateKeys,
        List<String> artifactIds,
        List<String> externalResourceUris,
        CheckpointIntegrity integrity,
        Instant createdAt,
        Map<String, Object> metadata
) {
    public CheckpointManifest {
        Objects.requireNonNull(checkpointId, "checkpointId cannot be null");
        Objects.requireNonNull(integrity, "integrity cannot be null");
        stateKeys = stateKeys == null ? List.of() : List.copyOf(stateKeys);
        artifactIds = artifactIds == null ? List.of() : List.copyOf(artifactIds);
        externalResourceUris = externalResourceUris == null ? List.of() : List.copyOf(externalResourceUris);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
