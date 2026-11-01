package tech.kayys.andalus.agent.capability;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable discovery descriptor for an agent.
 *
 * <p>A profile describes what an agent claims it can do. It deliberately
 * contains no credentials, live runtime handles, or permission grants.
 * Authorization must be evaluated independently at delegation time.</p>
 *
 * @param id stable agent identifier
 * @param name human-readable agent name
 * @param description concise agent purpose
 * @param capabilities capabilities advertised by the agent
 * @param metadata non-sensitive discovery metadata
 */
public record AgentProfile(
        String id,
        String name,
        String description,
        Set<AgentCapability> capabilities,
        Map<String, String> metadata) {

    public AgentProfile {
        id = requireText(id, "id");
        name = requireText(name, "name");
        description = requireText(description, "description");
        capabilities = capabilities == null ? Set.of() : Set.copyOf(capabilities);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public boolean supports(String capabilityId) {
        if (capabilityId == null || capabilityId.isBlank()) {
            return false;
        }
        return capabilities.stream()
                .anyMatch(capability -> capability.id().equals(capabilityId.trim()));
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
