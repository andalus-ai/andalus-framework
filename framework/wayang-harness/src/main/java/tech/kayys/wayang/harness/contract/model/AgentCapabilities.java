package tech.kayys.wayang.harness.contract.model;

import java.util.Set;

/**
 * Declared capabilities for an agent: required, optional, and delegatable.
 */
public record AgentCapabilities(
        Set<String> required,
        Set<String> optional,
        Set<String> delegatable
) {
    public AgentCapabilities {
        required = required != null ? Set.copyOf(required) : Set.of();
        optional = optional != null ? Set.copyOf(optional) : Set.of();
        delegatable = delegatable != null ? Set.copyOf(delegatable) : Set.of();
    }

    public static AgentCapabilities of(Set<String> required, Set<String> optional) {
        return new AgentCapabilities(required, optional, Set.of());
    }

    public static AgentCapabilities empty() {
        return new AgentCapabilities(Set.of(), Set.of(), Set.of());
    }
}
