package tech.kayys.andalus.anp.auth;

import java.security.PublicKey;
import java.util.Optional;

/**
 * Resolves a DID-authentication-authorized public key from an explicitly trusted source.
 * Implementations must not promote untrusted remote DID documents to trusted registrations.
 */
@FunctionalInterface
public interface AnpDidKeyResolver {
    Optional<PublicKey> resolveAuthenticationKey(String keyId);
}
