package tech.kayys.andalus.execution.conformance;

import java.util.Map;
import java.util.Objects;

/**
 * Execution context provided to a conformance test.
 */
public record ConformanceContext(
    ConformanceProfile profile,
    Map<String, Object> environmentAttributes,
    Map<String, Object> testParameters
) {
    public ConformanceContext {
        Objects.requireNonNull(profile, "profile cannot be null");
        environmentAttributes = environmentAttributes != null ? Map.copyOf(environmentAttributes) : Map.of();
        testParameters = testParameters != null ? Map.copyOf(testParameters) : Map.of();
    }

    public static ConformanceContext of(ConformanceProfile profile) {
        return new ConformanceContext(profile, Map.of(), Map.of());
    }

    public static ConformanceContext of(ConformanceProfile profile, Map<String, Object> env) {
        return new ConformanceContext(profile, env, Map.of());
    }
}
