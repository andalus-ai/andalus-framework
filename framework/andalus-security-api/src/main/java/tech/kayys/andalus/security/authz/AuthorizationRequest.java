package tech.kayys.andalus.security.authz;

import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.delegation.DelegationContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Authorization request expressed in Andalus semantics, not HTTP/protocol semantics.
 * The capability, action, and resource describe what the caller wants to do.
 */
public record AuthorizationRequest(
        SecurityContext securityContext,
        String capability,
        String action,
        String resource,
        Map<String, Object> attributes,
        Optional<DelegationContext> delegation
) {

    public AuthorizationRequest {
        Objects.requireNonNull(securityContext, "securityContext");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        delegation = delegation == null ? Optional.empty() : delegation;
    }

    /** Backwards-compatible constructor for non-delegated authorization requests. */
    public AuthorizationRequest(
            SecurityContext securityContext,
            String capability,
            String action,
            String resource,
            Map<String, Object> attributes
    ) {
        this(securityContext, capability, action, resource, attributes, Optional.empty());
    }

    public static AuthorizationRequest of(
            SecurityContext securityContext,
            String capability,
            String action,
            String resource
    ) {
        return new AuthorizationRequest(securityContext, capability, action, resource, Map.of());
    }

    public static AuthorizationRequest execute(SecurityContext securityContext, String capability) {
        return of(securityContext, capability, "execute", null);
    }

    public static AuthorizationRequest execute(
            SecurityContext securityContext,
            String capability,
            String resource,
            Optional<DelegationContext> delegation
    ) {
        return new AuthorizationRequest(
                securityContext, capability, "execute", resource, Map.of(), delegation);
    }
}
