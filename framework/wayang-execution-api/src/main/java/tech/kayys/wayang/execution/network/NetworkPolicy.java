package tech.kayys.wayang.execution.network;

import java.util.Set;

/**
 * Declarative network isolation policy for a sandbox.
 */
public record NetworkPolicy(
        NetworkMode mode,
        Set<NetworkRule> rules
) {

    public NetworkPolicy {
        mode = mode != null ? mode : NetworkMode.NONE;
        rules = rules != null ? Set.copyOf(rules) : Set.of();
    }

    public static NetworkPolicy disabled() {
        return new NetworkPolicy(NetworkMode.NONE, Set.of());
    }

    public static NetworkPolicy loopbackOnly() {
        return new NetworkPolicy(NetworkMode.LOOPBACK_ONLY, Set.of());
    }

    public static NetworkPolicy allowlist(Set<NetworkRule> rules) {
        return new NetworkPolicy(NetworkMode.ALLOWLIST, rules);
    }

    public static NetworkPolicy full() {
        return new NetworkPolicy(NetworkMode.FULL, Set.of());
    }
}
