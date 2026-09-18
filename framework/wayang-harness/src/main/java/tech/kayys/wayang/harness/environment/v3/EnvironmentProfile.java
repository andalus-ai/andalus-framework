package tech.kayys.wayang.harness.environment.v3;

import tech.kayys.wayang.harness.environment.v3.resource.ResourceRequirements;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Predefined environment profile packaging resources, capabilities, and policy parameters.
 */
public record EnvironmentProfile(
        String name,
        ResourceRequirements resources,
        Set<String> capabilities,
        Map<String, String> properties
) {
    public EnvironmentProfile {
        Objects.requireNonNull(name, "name");
        resources = resources != null ? resources : ResourceRequirements.none();
        capabilities = capabilities != null ? Set.copyOf(capabilities) : Set.of();
        properties = properties != null ? Map.copyOf(properties) : Map.of();
    }

    public static EnvironmentProfile coding() {
        return new EnvironmentProfile(
                "coding",
                ResourceRequirements.of(100L),
                Set.of("filesystem.read", "filesystem.write", "process.spawn", "workspace.snapshot"),
                Map.of("category", "development")
        );
    }

    public static EnvironmentProfile minimal() {
        return new EnvironmentProfile(
                "minimal",
                ResourceRequirements.none(),
                Set.of("filesystem.read"),
                Map.of("category", "restricted")
        );
    }
}
