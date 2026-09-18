package tech.kayys.wayang.spi.sandbox;

import tech.kayys.wayang.extension.Version;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record SandboxDescriptor(
        String id,
        String name,
        String description,
        SandboxType type,
        Version version,
        Set<String> features,
        Map<String, Object> attributes
) {

    public SandboxDescriptor {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException(
                    "Sandbox id cannot be null or blank"
            );
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException(
                    "Sandbox name cannot be null or blank"
            );
        }

        Objects.requireNonNull(
                type,
                "Sandbox type cannot be null"
        );

        Objects.requireNonNull(
                version,
                "Sandbox version cannot be null"
        );

        id = id.trim();
        name = name.trim();

        description = description == null
                ? ""
                : description.trim();

        features = features == null
                ? Set.of()
                : Set.copyOf(features);

        attributes = attributes == null
                ? Map.of()
                : Map.copyOf(attributes);
    }
}
