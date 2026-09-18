package tech.kayys.wayang.spi.sandbox;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public record ExecutionIsolationProfile(
        IsolationLevel level,
        Set<IsolationFeature> requiredFeatures,
        SandboxType preferredSandboxType,
        SandboxLimits limits,
        SandboxFilesystem filesystem,
        SandboxNetwork network,
        Map<String, String> environment,
        Map<String, Object> attributes
) {

    public ExecutionIsolationProfile {
        level = Objects.requireNonNull(level, "level");
        requiredFeatures = requiredFeatures == null
                ? Set.of()
                : Set.copyOf(requiredFeatures);

        preferredSandboxType = Objects.requireNonNull(
                preferredSandboxType,
                "preferredSandboxType");

        limits = Objects.requireNonNull(limits, "limits");
        filesystem = Objects.requireNonNull(filesystem, "filesystem");
        network = Objects.requireNonNull(network, "network");

        environment = environment == null
                ? Map.of()
                : Map.copyOf(environment);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public boolean requires(IsolationFeature feature) {
        return requiredFeatures.contains(feature);
    }

    public Optional<String> environmentVariable(String name) {
        return Optional.ofNullable(environment.get(name));
    }

    public static ExecutionIsolationProfile none() {
        return new ExecutionIsolationProfile(
                IsolationLevel.NONE,
                Set.of(),
                SandboxType.NONE,
                SandboxLimits.unlimited(),
                SandboxFilesystem.empty(),
                SandboxNetwork.full(),
                Map.of(),
                Map.of()
        );
    }
}
