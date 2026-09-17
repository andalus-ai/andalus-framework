package tech.kayys.wayang.harness.capability;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Configurable implementation of {@link CapabilityScope} supporting allowlists, denylists, wildcards, and approval predicates.
 */
public class DefaultCapabilityScope implements CapabilityScope {

    private final Set<String> allowedCapabilities;
    private final Set<String> deniedCapabilities;
    private final Set<String> requireApprovalCapabilities;
    private final Predicate<CapabilityRequest> approvalCondition;

    public DefaultCapabilityScope(
            Set<String> allowedCapabilities,
            Set<String> deniedCapabilities,
            Set<String> requireApprovalCapabilities,
            Predicate<CapabilityRequest> approvalCondition) {
        this.allowedCapabilities = allowedCapabilities != null ? Set.copyOf(allowedCapabilities) : Set.of("*");
        this.deniedCapabilities = deniedCapabilities != null ? Set.copyOf(deniedCapabilities) : Set.of();
        this.requireApprovalCapabilities = requireApprovalCapabilities != null ? Set.copyOf(requireApprovalCapabilities) : Set.of();
        this.approvalCondition = approvalCondition != null ? approvalCondition : (req -> false);
    }

    public static DefaultCapabilityScope allowAll() {
        return new DefaultCapabilityScope(Set.of("*"), Set.of(), Set.of(), null);
    }

    public static DefaultCapabilityScope of(Set<String> allowed) {
        return new DefaultCapabilityScope(allowed, Set.of(), Set.of(), null);
    }

    @Override
    public boolean allows(String capabilityId) {
        if (matchesPattern(deniedCapabilities, capabilityId)) {
            return false;
        }
        return matchesPattern(allowedCapabilities, capabilityId);
    }

    @Override
    public CapabilityDecision evaluate(CapabilityRequest request) {
        Objects.requireNonNull(request, "request");
        String id = request.capabilityId();

        if (matchesPattern(deniedCapabilities, id)) {
            return CapabilityDecision.deny("Capability explicitly denied by scope policy: " + id);
        }

        if (matchesPattern(requireApprovalCapabilities, id) || approvalCondition.test(request)) {
            return CapabilityDecision.requireApproval("Operation requires human-in-the-loop approval: " + id);
        }

        if (!allows(id)) {
            return CapabilityDecision.deny("Capability not permitted in this execution scope: " + id);
        }

        return CapabilityDecision.allow();
    }

    private boolean matchesPattern(Set<String> patterns, String capabilityId) {
        if (patterns.contains("*") || patterns.contains(capabilityId)) {
            return true;
        }
        for (String pattern : patterns) {
            if (pattern.endsWith(".*")) {
                String prefix = pattern.substring(0, pattern.length() - 2);
                if (capabilityId.startsWith(prefix)) {
                    return true;
                }
            }
        }
        return false;
    }
}
