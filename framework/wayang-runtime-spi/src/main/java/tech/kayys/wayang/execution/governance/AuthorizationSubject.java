package tech.kayys.wayang.execution.governance;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable identity model for authorization, representing tenant, user, agent, and granted roles.
 */
public record AuthorizationSubject(
        String tenantId,
        String userId,
        String agentId,
        List<String> roles
) {

    public AuthorizationSubject {
        tenantId = normalize(tenantId);
        userId = normalize(userId);
        agentId = normalize(agentId);

        roles = roles == null
                ? List.of()
                : roles.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(role -> !role.isBlank())
                        .distinct()
                        .toList();
    }

    private static String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public Optional<String> tenantIdOptional() {
        return Optional.ofNullable(tenantId);
    }

    public Optional<String> userIdOptional() {
        return Optional.ofNullable(userId);
    }

    public Optional<String> agentIdOptional() {
        return Optional.ofNullable(agentId);
    }

    public boolean hasRole(String role) {
        return role != null && roles.contains(role.trim());
    }

    public boolean isAnonymous() {
        return userId == null;
    }

    public static AuthorizationSubject of(String tenantId, String userId, String agentId, List<String> roles) {
        return new AuthorizationSubject(tenantId, userId, agentId, roles);
    }

    public static AuthorizationSubject standalone(String agentId) {
        return new AuthorizationSubject(null, null, agentId, List.of("*"));
    }
}
