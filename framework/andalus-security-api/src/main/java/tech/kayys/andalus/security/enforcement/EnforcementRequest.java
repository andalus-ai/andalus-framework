package tech.kayys.andalus.security.enforcement;

import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.delegation.DelegationContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Request submitted to a {@link PolicyEnforcementPoint}.
 * The payload {@code data} can be any domain payload (e.g. JSON, Map, text) that may undergo transformation.
 */
public record EnforcementRequest(
        SecurityContext securityContext,
        String capability,
        String action,
        String resource,
        Object data,
        Map<String, Object> attributes,
        Optional<DelegationContext> delegation
) {

    public EnforcementRequest {
        Objects.requireNonNull(securityContext, "securityContext");
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
        delegation = delegation == null ? Optional.empty() : delegation;
    }

    /** Backwards-compatible constructor for requests without delegated authority. */
    public EnforcementRequest(
            SecurityContext securityContext,
            String capability,
            String action,
            String resource,
            Object data,
            Map<String, Object> attributes
    ) {
        this(securityContext, capability, action, resource, data, attributes, Optional.empty());
    }

    public static EnforcementRequest of(
            SecurityContext securityContext,
            String capability,
            String action,
            String resource,
            Object data
    ) {
        return new EnforcementRequest(securityContext, capability, action, resource, data, Map.of());
    }

    public static EnforcementRequest of(
            SecurityContext securityContext,
            String capability,
            String action,
            String resource,
            Object data,
            Optional<DelegationContext> delegation
    ) {
        return new EnforcementRequest(securityContext, capability, action, resource, data, Map.of(), delegation);
    }

    public static EnforcementRequest of(SecurityContext securityContext, String capability, Object data) {
        return of(securityContext, capability, "execute", null, data);
    }
}
