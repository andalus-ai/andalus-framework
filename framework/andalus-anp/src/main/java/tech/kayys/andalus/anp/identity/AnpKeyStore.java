package tech.kayys.andalus.anp.identity;

import java.util.Optional;

/**
 * SPI for enterprise-grade management of DID:WBA signing keys.
 *
 * <p>Implementations may load keys from in-memory configuration, PKCS#12/JKS,
 * Vault, cloud KMS, or an HSM.
 *
 * <p>Inbound authentication uses {@link #getActiveKey(String)}, not {@link #getKey(String)}.
 * Implementations must explicitly opt in to active-key lookup. The default is fail-closed
 * so an older custom key store cannot accidentally bypass revocation enforcement.
 */
public interface AnpKeyStore {

    /** Retrieves key material for administrative or signing operations. */
    Optional<AnpIdentityKeyPair> getKey(String keyId);

    /**
     * Retrieves a key only when it is currently authorized for inbound authentication.
     * Custom implementations must override this method and enforce durable revocation state.
     */
    default Optional<AnpIdentityKeyPair> getActiveKey(String keyId) {
        return Optional.empty();
    }

    /**
     * Permanently revokes a key identifier. Rotation must register a new key ID.
     * Stores that do not support revocation must reject this operation.
     */
    default void revokeKey(String keyId) {
        throw new UnsupportedOperationException("This ANP key store does not support key revocation");
    }

    /** Resolves the default signing key ID for the given agent. */
    Optional<String> getAgentKeyId(String agentId);

    /** Stores or updates a key pair. A revoked key ID must never be reactivated. */
    void storeKey(String agentId, AnpIdentityKeyPair keyPair);
}
