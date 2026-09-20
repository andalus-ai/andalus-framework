package tech.kayys.wayang.execution.economics;

import tech.kayys.wayang.execution.resource.model.ResourceType;

import java.util.Objects;

/**
 * Hard upper bound on aggregate allocation for a given resource type within a scope.
 */
public record Quota(
    String quotaId,
    QuotaScope scope,
    ResourceType type,
    double maxAllowed
) {
    public Quota {
        Objects.requireNonNull(quotaId, "quotaId cannot be null");
        Objects.requireNonNull(scope, "scope cannot be null");
        Objects.requireNonNull(type, "type cannot be null");
    }

    public static Quota of(String quotaId, QuotaScope scope, ResourceType type, double maxAllowed) {
        return new Quota(quotaId, scope, type, maxAllowed);
    }
}
