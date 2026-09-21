package tech.kayys.andalus.security.propagation;

import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.delegation.DelegationContext;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable snapshot of a {@link SecurityContext} suitable for cross-boundary serialization and propagation.
 */
public record SecurityContextSnapshot(
        Principal principal,
        TenantContext tenant,
        Optional<DelegationContext> delegation,
        Map<String, Object> attributes
) {

    public SecurityContextSnapshot {
        Objects.requireNonNull(principal, "principal");
        Objects.requireNonNull(tenant, "tenant");
        delegation = delegation == null ? Optional.empty() : delegation;
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }

    public static SecurityContextSnapshot from(SecurityContext context, Optional<DelegationContext> delegation) {
        return new SecurityContextSnapshot(
                context.principal(),
                context.tenant(),
                delegation,
                context.attributes()
        );
    }
}
