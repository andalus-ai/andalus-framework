package tech.kayys.wayang.execution.artifact;

import java.util.Map;
import java.util.Set;

/**
 * Policy governing which paths/files from a sandbox are packaged and exported.
 */
public record ArtifactExportPolicy(
        Set<String> exportPaths,
        boolean compress,
        Map<String, String> tags
) {

    public ArtifactExportPolicy {
        exportPaths = exportPaths != null ? Set.copyOf(exportPaths) : Set.of();
        tags = tags != null ? Map.copyOf(tags) : Map.of();
    }

    public static ArtifactExportPolicy paths(Set<String> paths) {
        return new ArtifactExportPolicy(paths, false, Map.of());
    }
}
