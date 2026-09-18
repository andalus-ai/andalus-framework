package tech.kayys.wayang.harness.environment.v3;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Metadata descriptor for an execution environment substrate.
 */
public record EnvironmentDescriptor(
        EnvironmentId id,
        String name,
        String substrateType,
        Set<String> supportedCapabilities,
        Map<String, String> properties
) {
    public EnvironmentDescriptor {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        substrateType = substrateType != null ? substrateType : "local";
        supportedCapabilities = supportedCapabilities != null ? Set.copyOf(supportedCapabilities) : Set.of();
        properties = properties != null ? Map.copyOf(properties) : Map.of();
    }

    public static EnvironmentDescriptor localDefault(EnvironmentId id) {
        return new EnvironmentDescriptor(
                id,
                "local-default",
                "local",
                Set.of("filesystem.read", "filesystem.write", "process.spawn"),
                Map.of("os", System.getProperty("os.name", "unknown"))
        );
    }
}
