package tech.kayys.wayang.harness.authority;

import java.util.Map;
import java.util.Objects;

/**
 * Logical and network identity of a coordinator instance.
 */
public record CoordinationIdentity(
        CoordinatorId coordinatorId,
        String host,
        int port,
        Map<String, String> attributes
) {

    public CoordinationIdentity {
        Objects.requireNonNull(coordinatorId, "CoordinatorId cannot be null");
        host = host != null ? host : "localhost";
        attributes = attributes != null ? Map.copyOf(attributes) : Map.of();
    }

    public static CoordinationIdentity local(CoordinatorId id) {
        return new CoordinationIdentity(id, "localhost", 8080, Map.of());
    }
}
