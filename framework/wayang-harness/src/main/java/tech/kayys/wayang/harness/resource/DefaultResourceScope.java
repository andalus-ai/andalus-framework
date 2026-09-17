package tech.kayys.wayang.harness.resource;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Configurable implementation of {@link ResourceScope} governing resource access by type, ID, and condition.
 */
public class DefaultResourceScope implements ResourceScope {

    private final Set<ResourceType> allowedTypes;
    private final Set<ResourceType> deniedTypes;
    private final Set<ResourceType> requireApprovalTypes;
    private final Set<String> allowedResourceIds;
    private final Set<String> deniedResourceIds;
    private final Predicate<ResourceRequest> approvalCondition;

    public DefaultResourceScope(
            Set<ResourceType> allowedTypes,
            Set<ResourceType> deniedTypes,
            Set<ResourceType> requireApprovalTypes,
            Set<String> allowedResourceIds,
            Set<String> deniedResourceIds,
            Predicate<ResourceRequest> approvalCondition
    ) {
        this.allowedTypes = allowedTypes != null ? EnumSet.copyOf(allowedTypes) : EnumSet.allOf(ResourceType.class);
        this.deniedTypes = deniedTypes != null && !deniedTypes.isEmpty() ? EnumSet.copyOf(deniedTypes) : EnumSet.noneOf(ResourceType.class);
        this.requireApprovalTypes = requireApprovalTypes != null && !requireApprovalTypes.isEmpty() ? EnumSet.copyOf(requireApprovalTypes) : EnumSet.noneOf(ResourceType.class);
        this.allowedResourceIds = allowedResourceIds != null ? Set.copyOf(allowedResourceIds) : Set.of();
        this.deniedResourceIds = deniedResourceIds != null ? Set.copyOf(deniedResourceIds) : Set.of();
        this.approvalCondition = approvalCondition != null ? approvalCondition : req -> false;
    }

    public static DefaultResourceScope allowAll() {
        return new DefaultResourceScope(EnumSet.allOf(ResourceType.class), null, null, null, null, null);
    }

    public static DefaultResourceScope of(ResourceType... allowed) {
        EnumSet<ResourceType> set = EnumSet.noneOf(ResourceType.class);
        Collections.addAll(set, allowed);
        return new DefaultResourceScope(set, null, null, null, null, null);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean allows(ResourceRequest request) {
        return evaluate(request).isAllowed();
    }

    @Override
    public ResourceDecision evaluate(ResourceRequest request) {
        Objects.requireNonNull(request, "request");
        ResourceType type = request.type();
        String resourceId = request.resourceId();

        // 1. Explicit denial checks
        if (deniedTypes.contains(type)) {
            return ResourceDecision.deny("resource-scope-denied", "Resource type explicitly denied by scope policy: " + type);
        }
        if (resourceId != null && (deniedResourceIds.contains(resourceId) || matchesPattern(deniedResourceIds, resourceId))) {
            return ResourceDecision.deny("resource-scope-denied", "Resource ID explicitly denied by scope policy: " + resourceId);
        }

        // 2. Approval requirements
        if (requireApprovalTypes.contains(type) || approvalCondition.test(request)) {
            return ResourceDecision.requireApproval("resource-scope-approval", "Access to resource requires explicit approval: " + type + (resourceId != null ? " (" + resourceId + ")" : ""));
        }

        // 3. Allowed type check
        if (!allowedTypes.contains(type)) {
            return ResourceDecision.deny("resource-scope-type-unauthorized", "Resource type not permitted in execution scope: " + type);
        }

        // 4. Allowed resource ID check (if allowlist is populated)
        if (!allowedResourceIds.isEmpty() && resourceId != null) {
            if (!allowedResourceIds.contains(resourceId) && !matchesPattern(allowedResourceIds, resourceId)) {
                return ResourceDecision.deny("resource-scope-id-unauthorized", "Resource ID not permitted in execution scope: " + resourceId);
            }
        }

        return ResourceDecision.allow();
    }

    private boolean matchesPattern(Set<String> patterns, String value) {
        if (patterns.contains("*") || patterns.contains(value)) {
            return true;
        }
        for (String pattern : patterns) {
            if (pattern.endsWith("/*") || pattern.endsWith(".*")) {
                String prefix = pattern.substring(0, pattern.length() - 2);
                if (value.startsWith(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class Builder {
        private final Set<ResourceType> allowedTypes = new HashSet<>();
        private final Set<ResourceType> deniedTypes = new HashSet<>();
        private final Set<ResourceType> requireApprovalTypes = new HashSet<>();
        private final Set<String> allowedResourceIds = new HashSet<>();
        private final Set<String> deniedResourceIds = new HashSet<>();
        private Predicate<ResourceRequest> approvalCondition = req -> false;

        public Builder allow(ResourceType... types) {
            Collections.addAll(allowedTypes, types);
            return this;
        }

        public Builder deny(ResourceType... types) {
            Collections.addAll(deniedTypes, types);
            return this;
        }

        public Builder requireApproval(ResourceType... types) {
            Collections.addAll(requireApprovalTypes, types);
            return this;
        }

        public Builder allowResourceId(String id) {
            allowedResourceIds.add(id);
            return this;
        }

        public Builder denyResourceId(String id) {
            deniedResourceIds.add(id);
            return this;
        }

        public Builder approvalCondition(Predicate<ResourceRequest> condition) {
            this.approvalCondition = Objects.requireNonNull(condition, "condition");
            return this;
        }

        public DefaultResourceScope build() {
            Set<ResourceType> allowed = allowedTypes.isEmpty() ? EnumSet.allOf(ResourceType.class) : allowedTypes;
            return new DefaultResourceScope(allowed, deniedTypes, requireApprovalTypes, allowedResourceIds, deniedResourceIds, approvalCondition);
        }
    }
}
