package tech.kayys.wayang.harness.artifact;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * Request to import an external file or resource into Wayang as a governed artifact.
 */
public record ImportRequest(
        URI source,
        String name,
        ArtifactType type,
        Map<String, String> properties
) {
    public ImportRequest {
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(name, "name");
        type = type != null ? type : ArtifactType.CUSTOM;
        properties = properties != null ? Map.copyOf(properties) : Map.of();
    }
}
