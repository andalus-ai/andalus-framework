package tech.kayys.andalus.skill.security;

import java.util.Map;
import java.util.Optional;

/**
 * Governed execution security context for a Skill invocation.
 *
 * <p>Separated from {@code SkillContext} so that generic Skill implementations
 * are not coupled to security/IAM or multitenancy concerns.</p>
 */
public interface SkillSecurityContext {

    /**
     * Whether the invocation caller has been authenticated.
     */
    boolean authenticated();

    /**
     * Optional authenticated principal identity (user, service, or agent).
     */
    Optional<String> principal();

    /**
     * Security attributes (roles, permissions, scopes, tenant identifiers).
     */
    Map<String, Object> attributes();

    /**
     * Create an unauthenticated context with no attributes.
     */
    static SkillSecurityContext unauthenticated() {
        return new SkillSecurityContext() {
            @Override public boolean authenticated() { return false; }
            @Override public Optional<String> principal() { return Optional.empty(); }
            @Override public Map<String, Object> attributes() { return Map.of(); }
        };
    }

    /**
     * Create an authenticated context with the given principal name.
     */
    static SkillSecurityContext authenticated(String principal, Map<String, Object> attributes) {
        Map<String, Object> copy = attributes == null ? Map.of() : Map.copyOf(attributes);
        return new SkillSecurityContext() {
            @Override public boolean authenticated() { return true; }
            @Override public Optional<String> principal() { return Optional.ofNullable(principal); }
            @Override public Map<String, Object> attributes() { return copy; }
        };
    }
}
