package tech.kayys.andalus.agent.capability;

import java.util.Objects;

/**
 * A capability an agent explicitly advertises to the Andalus agent directory.
 *
 * <p>Capability identifiers are stable, namespaced values (for example
 * {@code research.web-search} or {@code finance.forecasting}). Descriptions
 * are intended for humans and discovery tooling; they are not executable
 * instructions and must never be treated as authorization.</p>
 *
 * @param id stable capability identifier
 * @param description concise description of the capability
 */
public record AgentCapability(String id, String description) {

    public AgentCapability {
        id = requireText(id, "id");
        description = requireText(description, "description");
    }

    private static String requireText(String value, String field) {
        Objects.requireNonNull(value, field + " must not be null");
        String normalized = value.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return normalized;
    }
}
