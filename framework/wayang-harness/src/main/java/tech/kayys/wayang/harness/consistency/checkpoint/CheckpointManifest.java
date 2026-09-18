package tech.kayys.wayang.harness.consistency.checkpoint;

import java.util.Map;
import java.util.Set;

/**
 * Manifest defining compositional snapshot slices across harness subsystems.
 */
public record CheckpointManifest(
        Map<String, Object> stateData,
        Set<String> artifactIds,
        Map<String, Object> contextReferences,
        long sequence
) {

    public CheckpointManifest {
        stateData = stateData != null ? Map.copyOf(stateData) : Map.of();
        artifactIds = artifactIds != null ? Set.copyOf(artifactIds) : Set.of();
        contextReferences = contextReferences != null ? Map.copyOf(contextReferences) : Map.of();
    }

    public static CheckpointManifest of(
            Map<String, Object> stateData,
            Set<String> artifactIds,
            Map<String, Object> contextReferences,
            long sequence
    ) {
        return new CheckpointManifest(stateData, artifactIds, contextReferences, sequence);
    }

    public static CheckpointManifest empty(long sequence) {
        return new CheckpointManifest(Map.of(), Set.of(), Map.of(), sequence);
    }
}
