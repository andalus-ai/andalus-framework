package tech.kayys.wayang.spi.sandbox;

import java.util.Objects;
import java.util.Set;

public record IsolationRequirement(
        IsolationLevel minimumLevel,
        Set<IsolationFeature> requiredFeatures,
        SandboxType preferredType
) {

    public IsolationRequirement {
        minimumLevel = Objects.requireNonNull(
                minimumLevel,
                "minimumLevel");

        requiredFeatures = requiredFeatures == null
                ? Set.of()
                : Set.copyOf(requiredFeatures);

        preferredType = Objects.requireNonNull(
                preferredType,
                "preferredType");
    }

    public boolean requires(IsolationFeature feature) {
        return requiredFeatures.contains(feature);
    }
}
