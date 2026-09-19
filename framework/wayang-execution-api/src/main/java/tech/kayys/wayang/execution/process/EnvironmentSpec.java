package tech.kayys.wayang.execution.process;

import java.util.Map;
import java.util.Set;

/**
 * Declares environment variables configuration for a process invocation.
 */
public record EnvironmentSpec(
        Map<String, String> variables,
        Set<String> inheritedKeys,
        boolean inheritHostEnvironment
) {

    public EnvironmentSpec {
        variables = variables != null ? Map.copyOf(variables) : Map.of();
        inheritedKeys = inheritedKeys != null ? Set.copyOf(inheritedKeys) : Set.of();
    }

    public static EnvironmentSpec empty() {
        return new EnvironmentSpec(Map.of(), Set.of(), false);
    }

    public static EnvironmentSpec of(Map<String, String> variables) {
        return new EnvironmentSpec(variables, Set.of(), false);
    }
}
