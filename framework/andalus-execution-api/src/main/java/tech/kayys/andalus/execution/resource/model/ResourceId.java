package tech.kayys.andalus.execution.resource.model;

import java.util.Objects;

/**
 * Stable logical identity of a virtual execution resource.
 */
public record ResourceId(String value) {
    public ResourceId {
        Objects.requireNonNull(value, "value cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("value cannot be blank");
        }
    }

    public static ResourceId of(String value) {
        return new ResourceId(value);
    }

    public static ResourceId of(String category, String name) {
        return new ResourceId("resource://" + category + "/" + name);
    }
}
