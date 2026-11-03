package tech.kayys.andalus.security.propagation;

import tech.kayys.andalus.security.context.SecurityContext;
import tech.kayys.andalus.security.delegation.DelegationContext;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable snapshot of a {@link SecurityContext} suitable for cross-boundary
 * serialization and explicit propagation.
 *
 * <p>Consumers should capture this value at a trusted execution boundary and
 * restore it inside asynchronous callbacks or worker executions. The snapshot
 * is data, not proof of authentication: remote transports must authenticate
 * and validate it before accepting it.</p>
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
        Objects.requireNonNull(context, "context");
        return new SecurityContextSnapshot(
                context.principal(),
                context.tenant(),
                delegation,
                context.attributes()
        );
    }

    /**
     * Reconstructs the security context represented by this snapshot.
     *
     * <p>This method does not install ambient or thread-local state. Callers
     * must pass the returned context explicitly to the operation being run.
     * Delegation remains available separately through {@link #delegation()}.</p>
     */
    public SecurityContext restore() {
        return new SecurityContext(principal, tenant, attributes);
    }
}
