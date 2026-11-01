package tech.kayys.andalus.security.delegation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Attenuation constraints that restrict the authority passed down the delegation chain.
 *
 * <p>Wildcard grants are deliberately opt-in through {@link #unrestricted()}.
 * Missing constraints fail closed and grant no capabilities or actions.</p>
 */
public record DelegationConstraints(
        List<String> allowedCapabilities,
        List<String> allowedActions,
        int maxHops
) {

    public DelegationConstraints {
        allowedCapabilities = allowedCapabilities == null ? List.of() : List.copyOf(allowedCapabilities);
        allowedActions = allowedActions == null ? List.of() : List.copyOf(allowedActions);
        if (maxHops < 0) {
            throw new IllegalArgumentException("maxHops must not be negative");
        }
        if (allowedCapabilities.stream().anyMatch(value -> value == null || value.isBlank())
                || allowedActions.stream().anyMatch(value -> value == null || value.isBlank())) {
            throw new IllegalArgumentException("Delegated capabilities and actions must be non-blank");
        }
    }

    public static DelegationConstraints unrestricted() {
        return new DelegationConstraints(List.of("*"), List.of("*"), 5);
    }

    public static DelegationConstraints empty() {
        return new DelegationConstraints(List.of(), List.of(), 0);
    }

    public static DelegationConstraints of(List<String> capabilities, int maxHops) {
        return new DelegationConstraints(capabilities, List.of(), maxHops);
    }

    public boolean permitsCapability(String capability) {
        return permits(allowedCapabilities, capability);
    }

    public boolean permitsAction(String action) {
        return permits(allowedActions, action);
    }

    public boolean canDelegate() {
        return maxHops > 0 && (!allowedCapabilities.isEmpty() || !allowedActions.isEmpty());
    }

    /**
     * Returns a child grant that cannot exceed either the parent's authority or
     * the requested authority. A wildcard in the child request does not widen
     * a restricted parent grant.
     */
    public DelegationConstraints attenuate(DelegationConstraints requested) {
        Objects.requireNonNull(requested, "requested");
        return new DelegationConstraints(
                intersect(allowedCapabilities, requested.allowedCapabilities),
                intersect(allowedActions, requested.allowedActions),
                Math.min(maxHops, requested.maxHops)
        );
    }

    public DelegationConstraints decrementHop() {
        if (maxHops == 0) {
            throw new IllegalStateException("Delegation hop limit exhausted");
        }
        return new DelegationConstraints(allowedCapabilities, allowedActions, maxHops - 1);
    }

    private static boolean permits(List<String> grants, String requested) {
        return requested != null && !requested.isBlank()
                && (grants.contains("*") || grants.contains(requested));
    }

    private static List<String> intersect(List<String> parent, List<String> child) {
        if (parent.contains("*")) {
            return List.copyOf(child);
        }
        if (child.contains("*")) {
            return List.copyOf(parent);
        }
        return parent.stream().filter(child::contains).collect(Collectors.toUnmodifiableList());
    }
}
