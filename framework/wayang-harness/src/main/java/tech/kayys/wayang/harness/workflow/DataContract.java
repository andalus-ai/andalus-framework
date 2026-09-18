package tech.kayys.wayang.harness.workflow;

import java.util.Map;
import java.util.Objects;

/**
 * Formal typing and schema contract for data flowing between nodes.
 */
public record DataContract(
        String mediaType,
        String schema,
        Map<String, Object> constraints
) {

    public DataContract {
        mediaType = mediaType != null ? mediaType : "application/octet-stream";
        schema = schema != null ? schema : "";
        constraints = constraints != null ? Map.copyOf(constraints) : Map.of();
    }

    public static DataContract standard(String mediaType) {
        return new DataContract(mediaType, "", Map.of());
    }

    public static DataContract json() {
        return new DataContract("application/json", "", Map.of());
    }

    public static DataContract text() {
        return new DataContract("text/plain", "", Map.of());
    }
}
