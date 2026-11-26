package tech.kayys.andalus.execution;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable, framework-neutral snapshot of the caller authenticated at an API
 * or transport boundary. Implementations must construct this value from
 * verified identity data; request payloads and checkpoint metadata are not
 * trusted sources for its claims.
 *
 * <p>Ownership fields identify who initiated an execution. Roles and
 * permissions are current authorization claims and must be supplied again
 * when a suspended execution is resumed; they must not be restored from a
 * checkpoint.</p>
 */
public record AgentCallerContext(
        String tenantId,
        String userId,
        PrincipalType principalType,
        String principalId,
        Set<String> roles,
        Set<String> permissions,
        Map<String, Object> attributes) {

    public AgentCallerContext {
        principalType = Objects.requireNonNull(principalType, "principalType");
        roles = immutableClaims(roles, "roles");
        permissions = immutableClaims(permissions, "permissions");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);

        if (principalType == PrincipalType.USER) {
            userId = required(userId, "userId");
            principalId = required(principalId, "principalId");
        } else if (principalType == PrincipalType.ANONYMOUS) {
            if (!roles.isEmpty() || !permissions.isEmpty()) {
                throw new IllegalArgumentException("anonymous callers cannot carry roles or permissions");
            }
            userId = null;
            principalId = null;
        } else {
            principalId = required(principalId, "principalId");
            if (userId != null && userId.isBlank()) {
                userId = null;
            }
        }
        if (tenantId != null && tenantId.isBlank()) {
            tenantId = null;
        }
    }

    public static AgentCallerContext anonymous(String tenantId) {
        return new AgentCallerContext(tenantId, null, PrincipalType.ANONYMOUS,
                null, Set.of(), Set.of(), Map.of());
    }

    public static AgentCallerContext user(
            String tenantId, String userId, Set<String> roles, Set<String> permissions) {
        return new AgentCallerContext(tenantId, userId, PrincipalType.USER, userId,
                roles, permissions, Map.of());
    }

    private static Set<String> immutableClaims(Set<String> claims, String name) {
        if (claims == null || claims.isEmpty()) {
            return Set.of();
        }
        Set<String> copy = Set.copyOf(claims);
        if (copy.stream().anyMatch(value -> value == null || value.isBlank()
                || "*".equals(value.trim()))) {
            throw new IllegalArgumentException(name + " must contain only concrete, non-blank claims");
        }
        return copy;
    }

    private static String required(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }

    public enum PrincipalType {
        USER,
        SERVICE,
        SYSTEM,
        ANONYMOUS
    }
}
