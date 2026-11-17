package tech.kayys.andalus.anp.security;

import tech.kayys.andalus.anp.auth.AnpAuthVerifier;
import tech.kayys.andalus.anp.auth.AnpHttpSignature;
import tech.kayys.andalus.anp.identity.DidWbaIdentity;
import tech.kayys.andalus.security.delegation.DelegationContext;
import tech.kayys.andalus.security.identity.Principal;
import tech.kayys.andalus.security.propagation.SecurityContextSnapshot;
import tech.kayys.andalus.security.tenant.TenantContext;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Maps an authenticated ANP request into an immutable {@link SecurityContextSnapshot}.
 *
 * <p>The mapper verifies the HTTP signature itself. A parsed signature is untrusted input;
 * callers must not mark a request as authenticated merely because a signature header exists.
 */
public final class AnpSecurityMapper {

    private final AnpPrincipalResolver principalResolver;
    private final AnpAuthVerifier authVerifier;

    public AnpSecurityMapper(
            AnpPrincipalResolver principalResolver,
            AnpAuthVerifier authVerifier) {
        this.principalResolver = Objects.requireNonNull(principalResolver, "principalResolver");
        this.authVerifier = Objects.requireNonNull(authVerifier, "authVerifier");
    }

    /**
     * Verifies the signature over the supplied request target before creating a security snapshot.
     *
     * <p>{@code tenantId} and {@code delegation} must already have been resolved by trusted
     * server-side policy. They must never be copied directly from untrusted ANP metadata.
     *
     * @throws SecurityException when the signature cannot be verified
     */
    public SecurityContextSnapshot map(
            DidWbaIdentity callerDid,
            String method,
            String path,
            String host,
            AnpHttpSignature signature,
            String tenantId,
            Optional<DelegationContext> delegation) {

        Objects.requireNonNull(callerDid, "callerDid");
        Objects.requireNonNull(signature, "signature");
        Objects.requireNonNull(delegation, "delegation");

        String callerKeyPrefix = callerDid.raw() + "#";
        if (!signature.keyId().startsWith(callerKeyPrefix)
                || signature.keyId().length() == callerKeyPrefix.length()) {
            throw new SecurityException("ANP signing key does not belong to the claimed caller DID");
        }

        if (!authVerifier.verify(method, path, host, signature)) {
            throw new SecurityException("ANP request signature verification failed");
        }

        Principal principal = principalResolver.resolve(callerDid);
        TenantContext tenant = (tenantId == null || tenantId.isBlank())
                ? TenantContext.empty()
                : TenantContext.of(tenantId);

        Map<String, Object> attributes = Map.of(
                "anp.keyId", signature.keyId(),
                "anp.algorithm", signature.algorithm(),
                "anp.verified", true
        );

        return new SecurityContextSnapshot(principal, tenant, delegation, attributes);
    }
}
