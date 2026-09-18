package tech.kayys.wayang.spi.sandbox;

import tech.kayys.wayang.extension.Version;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record SandboxProviderDescriptor(
        String id,
        String name,
        String description,
        Version version,
        Set<SandboxType> supportedTypes,
        Set<IsolationFeature> features,
        Set<NetworkIsolationFeature> networkFeatures,
        Set<ResourceLimitFeature> resourceLimitFeatures,
        Map<String, Object> attributes
) {

    public SandboxProviderDescriptor(
            String id,
            String name,
            String description,
            Version version,
            Set<SandboxType> supportedTypes,
            Set<IsolationFeature> features,
            Map<String, Object> attributes) {
        this(
                id,
                name,
                description,
                version,
                supportedTypes,
                features,
                Set.of(),
                Set.of(),
                attributes
        );
    }

    public SandboxProviderDescriptor {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Provider id cannot be null or blank"
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Provider name cannot be null or blank"
            );
        }

        Objects.requireNonNull(
                version,
                "version cannot be null"
        );

        id = id.trim();
        name = name.trim();

        description = description == null
                ? ""
                : description.trim();

        supportedTypes = supportedTypes == null
                ? Set.of()
                : Set.copyOf(supportedTypes);

        features = features == null
                ? Set.of()
                : Set.copyOf(features);

        networkFeatures = networkFeatures == null
                ? Set.of()
                : Set.copyOf(networkFeatures);

        resourceLimitFeatures = resourceLimitFeatures == null
                ? Set.of()
                : Set.copyOf(resourceLimitFeatures);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }

    public boolean supports(SandboxType type) {
        return supportedTypes.contains(type);
    }

    public boolean supports(IsolationFeature feature) {
        return features.contains(feature);
    }

    public boolean supportsAll(Set<IsolationFeature> required) {
        return features.containsAll(required);
    }

    public boolean supports(NetworkIsolationFeature feature) {
        return networkFeatures.contains(feature);
    }

    public boolean supports(ResourceLimitFeature feature) {
        return resourceLimitFeatures.contains(feature);
    }

    public boolean supportsAllResourceLimits(Set<ResourceLimitFeature> required) {
        return resourceLimitFeatures.containsAll(required);
    }
}
