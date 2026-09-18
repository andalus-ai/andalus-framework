package tech.kayys.wayang.harness.tool;

import tech.kayys.wayang.harness.context.HarnessIdentity;
import tech.kayys.wayang.harness.resource.ResourceScope;

import java.util.Map;

/**
 * Represents a tool request context.
 *
 * <p>Its components capture `identity`, `resources`, `metadata`.</p>
 *
 * @param identity the identity
 * @param resources the resources
 * @param metadata the metadata
 */


public record ToolRequestContext(
        HarnessIdentity identity,
        ResourceScope resources,
        Map<String, Object> metadata
) {
    public ToolRequestContext {
        metadata = metadata != null ? Map.copyOf(metadata) : Map.of();
    }

    public static ToolRequestContext of(HarnessIdentity identity, ResourceScope resources) {
        return new ToolRequestContext(identity, resources, Map.of());
    }

    public static ToolRequestContext of(HarnessIdentity identity, ResourceScope resources, Map<String, Object> metadata) {
        return new ToolRequestContext(identity, resources, metadata);
    }
}
