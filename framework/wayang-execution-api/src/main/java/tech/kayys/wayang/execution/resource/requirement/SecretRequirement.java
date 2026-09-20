package tech.kayys.wayang.execution.resource.requirement;

import tech.kayys.wayang.execution.resource.model.ResourceId;
import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.util.Objects;

/**
 * Secret requirement specification referencing a secure secret uri.
 */
public record SecretRequirement(
        ResourceId resourceId,
        String secretUri,
        String scope
) implements ResourceRequirement {
    public SecretRequirement {
        Objects.requireNonNull(resourceId, "resourceId cannot be null");
        Objects.requireNonNull(secretUri, "secretUri cannot be null");
        scope = scope == null ? "execution" : scope;
    }

    @Override
    public ResourceType type() {
        return ResourceType.SECRET;
    }

    public static SecretRequirement of(ResourceId id, String secretUri) {
        return new SecretRequirement(id, secretUri, "execution");
    }
}
