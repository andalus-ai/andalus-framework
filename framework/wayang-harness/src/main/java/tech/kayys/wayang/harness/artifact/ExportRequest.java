package tech.kayys.wayang.harness.artifact;

import java.net.URI;
import java.util.Map;
import java.util.Objects;

/**
 * Request to export an artifact to an external destination.
 */
public record ExportRequest(
        URI destination,
        String format,
        Map<String, String> options
) {
    public ExportRequest {
        Objects.requireNonNull(destination, "destination");
        format = format != null ? format : "raw";
        options = options != null ? Map.copyOf(options) : Map.of();
    }

    public static ExportRequest toUri(URI destination) {
        return new ExportRequest(destination, "raw", Map.of());
    }
}
