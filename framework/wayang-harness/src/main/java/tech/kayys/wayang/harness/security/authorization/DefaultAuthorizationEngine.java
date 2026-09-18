package tech.kayys.wayang.harness.security.authorization;

import tech.kayys.wayang.security.capability.CapabilityConstraints;

import java.util.Objects;
import java.util.Set;

/**
 * Default role-based and capability pattern-matching authorization engine.
 */
public class DefaultAuthorizationEngine implements AuthorizationEngine {

    private final Set<String> allowedCapabilities;
    private final Set<String> deniedCapabilities;

    public DefaultAuthorizationEngine(Set<String> allowed, Set<String> denied) {
        this.allowedCapabilities = allowed != null ? Set.copyOf(allowed) : Set.of("*");
        this.deniedCapabilities = denied != null ? Set.copyOf(denied) : Set.of();
    }

    public static DefaultAuthorizationEngine allowAll() {
        return new DefaultAuthorizationEngine(Set.of("*"), Set.of());
    }

    @Override
    public AuthorizationDecision authorize(AuthorizationRequest request) {
        Objects.requireNonNull(request, "request");
        String cap = request.capabilityId();

        if (matches(deniedCapabilities, cap)) {
            return new AuthorizationDecision.Denied("Capability explicitly forbidden: " + cap);
        }

        if (matches(allowedCapabilities, cap)) {
            return new AuthorizationDecision.Allowed(request.requestedConstraints(), "Permitted by policy");
        }

        return new AuthorizationDecision.Denied("Capability not authorized for principal: " + cap);
    }

    private boolean matches(Set<String> patterns, String capability) {
        if (patterns.contains("*") || patterns.contains(capability)) return true;
        for (String p : patterns) {
            if (p.endsWith("*") && capability.startsWith(p.substring(0, p.length() - 1))) {
                return true;
            }
        }
        return false;
    }
}
