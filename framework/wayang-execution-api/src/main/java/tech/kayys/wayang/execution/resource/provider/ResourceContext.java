package tech.kayys.wayang.execution.resource.provider;

import java.util.Map;
import java.util.Objects;

/**
 * Context provided to resource providers during provisioning and release.
 */
public record ResourceContext(
        String executionId,
        String runtimeId,
        Map<String, Object> environment
) {
    public ResourceContext {
        Objects.requireNonNull(executionId, "executionId cannot be null");
        Objects.requireNonNull(runtimeId, "runtimeId cannot be null");
        environment = environment == null ? Map.of() : Map.copyOf(environment);
    }

    public static ResourceContext of(String executionId, String runtimeId) {
        return new ResourceContext(executionId, runtimeId, Map.of());
    }
}
