package tech.kayys.wayang.harness.resource;

import tech.kayys.wayang.harness.environment.ResourceId;

import java.util.Objects;

/**
 * Immutable value record implementing {@link HarnessResource}.
 */
public record DefaultHarnessResource(
        ResourceId id,
        ResourceType type,
        ResourceStatus status,
        ResourceMetadata metadata
) implements HarnessResource {

    public DefaultHarnessResource {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(type, "type");
        status = status == null ? ResourceStatus.AVAILABLE : status;
        metadata = metadata == null ? ResourceMetadata.empty() : metadata;
    }

    public static DefaultHarnessResource of(String id, ResourceType type) {
        return new DefaultHarnessResource(ResourceId.of(id), type, ResourceStatus.AVAILABLE, ResourceMetadata.empty());
    }
}
