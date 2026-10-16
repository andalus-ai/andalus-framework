package tech.kayys.andalus.spi.agent;

import java.util.Set;

/**
 * Capabilities advertised by an agent.
 *
 * <p>Capability identifiers refer to the existing Andalus capability SPI.
 * They are opaque identifiers to the Agent Contract.</p>
 */
public record AgentCapabilities(
        Set<String> required,
        Set<String> optional,
        Set<String> delegatable
) {

    public AgentCapabilities {
        required = required == null
                ? Set.of()
                : Set.copyOf(required);

        optional = optional == null
                ? Set.of()
                : Set.copyOf(optional);

        delegatable = delegatable == null
                ? Set.of()
                : Set.copyOf(delegatable);
    }

    public static AgentCapabilities empty() {
        return new AgentCapabilities(
                Set.of(),
                Set.of(),
                Set.of());
    }

    public boolean requires(String capability) {
        return required.contains(capability);
    }

    public boolean supportsOptional(String capability) {
        return optional.contains(capability);
    }

    public boolean canDelegate(String capability) {
        return delegatable.contains(capability);
    }
}
