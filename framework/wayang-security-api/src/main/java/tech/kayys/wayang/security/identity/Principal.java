package tech.kayys.wayang.security.identity;

import java.util.Map;
import java.util.Objects;

/**
 * Immutable representation of the caller identity inside the Wayang security model.
 * Does not carry JWT tokens, Keycloak references, or protocol-specific details.
 */
public record Principal(
        String id,
        String name,
        IdentityType type,
        Map<String, Object> attributes
) {

    public Principal {
        Objects.requireNonNull(id,   "id");
        Objects.requireNonNull(type, "type");

        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static Principal anonymous() {
        return new Principal("anonymous", "anonymous", IdentityType.ANONYMOUS, Map.of());
    }

    public static Principal system() {
        return new Principal("system", "system", IdentityType.SYSTEM, Map.of());
    }

    public static Principal agent(String id, String name) {
        return new Principal(id, name, IdentityType.AGENT, Map.of());
    }

    public static Principal agent(String id, java.util.Set<String> roles) {
        return new Principal(id, id, IdentityType.AGENT, Map.of("roles", roles != null ? java.util.Set.copyOf(roles) : java.util.Set.of()));
    }

    public static Principal user(String id, String name) {
        return new Principal(id, name, IdentityType.USER, Map.of());
    }

    public static Principal user(String id, java.util.Set<String> roles) {
        return new Principal(id, id, IdentityType.USER, Map.of("roles", roles != null ? java.util.Set.copyOf(roles) : java.util.Set.of()));
    }

    public java.util.Set<String> roles() {
        Object r = attributes.get("roles");
        if (r instanceof java.util.Set<?> s) {
            @SuppressWarnings("unchecked")
            java.util.Set<String> cast = (java.util.Set<String>) s;
            return cast;
        } else if (r instanceof java.util.Collection<?> c) {
            return c.stream().map(Object::toString).collect(java.util.stream.Collectors.toUnmodifiableSet());
        }
        return java.util.Set.of();
    }
}
