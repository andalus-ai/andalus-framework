package tech.kayys.andalus.harness.model;

import tech.kayys.andalus.harness.context.HarnessIdentity;
import tech.kayys.andalus.harness.resource.ResourceScope;

import java.util.Map;

/**
 * Represents a model request context.
 *
 * <p>Its components capture `identity`, `resources`, `metadata`.</p>
 *
 * @param identity the identity
 * @param resources the resources
 * @param metadata the metadata
 */


public record ModelRequestContext(
        HarnessIdentity identity,
        ResourceScope resources,
        Map<String, Object> metadata
) {
    public ModelRequestContext {
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static ModelRequestContext of(HarnessIdentity identity, ResourceScope resources) {
        return new ModelRequestContext(identity, resources, Map.of());
    }
}
