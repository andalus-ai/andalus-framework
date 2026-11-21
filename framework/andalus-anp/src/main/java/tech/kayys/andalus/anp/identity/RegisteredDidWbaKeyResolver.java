package tech.kayys.andalus.anp.identity;

import tech.kayys.andalus.anp.auth.AnpDidKeyResolver;

import java.security.PublicKey;
import java.util.Objects;
import java.util.Optional;

/**
 * Resolves keys only from the resolver's locally registered (trusted) DID documents.
 * Optional remote fetching on DidWbaResolver is deliberately not used for authentication.
 */
public final class RegisteredDidWbaKeyResolver implements AnpDidKeyResolver {
    private final DidWbaResolver resolver;

    public RegisteredDidWbaKeyResolver(DidWbaResolver resolver) {
        this.resolver = Objects.requireNonNull(resolver, "resolver");
    }

    @Override
    public Optional<PublicKey> resolveAuthenticationKey(String keyId) {
        if (keyId == null || keyId.isBlank()) {
            return Optional.empty();
        }
        int separator = keyId.indexOf('#');
        if (separator <= 0 || separator == keyId.length() - 1) {
            return Optional.empty();
        }
        try {
            DidWbaIdentity identity = DidWbaIdentity.parse(keyId.substring(0, separator));
            return resolver.findRegistered(identity)
                    .map(document -> DidWbaPublicKeyResolver.resolveAuthenticationKey(document, keyId));
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }
}
