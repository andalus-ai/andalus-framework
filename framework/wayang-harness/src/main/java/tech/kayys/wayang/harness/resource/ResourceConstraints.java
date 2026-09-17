package tech.kayys.wayang.harness.resource;

import java.util.Map;
import java.util.OptionalLong;

/**
 * Multi-dimensional boundary constraints on a resource request.
 */
public record ResourceConstraints(
        OptionalLong minimum,
        OptionalLong maximum,
        Map<String, String> attributes
) {

    public ResourceConstraints {
        minimum = minimum == null ? OptionalLong.empty() : minimum;
        maximum = maximum == null ? OptionalLong.empty() : maximum;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static ResourceConstraints unconstrained() {
        return new ResourceConstraints(OptionalLong.empty(), OptionalLong.empty(), Map.of());
    }

    public static ResourceConstraints max(long maximum) {
        return new ResourceConstraints(OptionalLong.empty(), OptionalLong.of(maximum), Map.of());
    }

    public static ResourceConstraints range(long min, long max) {
        return new ResourceConstraints(OptionalLong.of(min), OptionalLong.of(max), Map.of());
    }

    public static ResourceConstraints withMaximum(long maximum) {
        return max(maximum);
    }

    public static ResourceConstraints withMinimum(long minimum) {
        return new ResourceConstraints(OptionalLong.of(minimum), OptionalLong.empty(), Map.of());
    }
}
