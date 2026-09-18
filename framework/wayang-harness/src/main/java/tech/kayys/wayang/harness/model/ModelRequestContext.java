package tech.kayys.wayang.harness.model;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.resource.ResourceScope;

import java.util.Map;

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
