package tech.kayys.andalus.execution.resource.binding;

import tech.kayys.andalus.execution.resource.model.ResourceId;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Collection of active bindings for an execution environment.
 */
public record ResourceBindings(Map<ResourceId, ResourceBinding> bindings) {
    public ResourceBindings {
        bindings = bindings == null ? Map.of() : Map.copyOf(bindings);
    }

    public static ResourceBindings of(Map<ResourceId, ResourceBinding> bindings) {
        return new ResourceBindings(bindings);
    }

    public static ResourceBindings empty() {
        return new ResourceBindings(Map.of());
    }

    public Optional<ResourceBinding> get(ResourceId id) {
        Objects.requireNonNull(id, "id cannot be null");
        return Optional.ofNullable(bindings.get(id));
    }

    public boolean contains(ResourceId id) {
        return bindings.containsKey(id);
    }

    public int size() {
        return bindings.size();
    }
}
