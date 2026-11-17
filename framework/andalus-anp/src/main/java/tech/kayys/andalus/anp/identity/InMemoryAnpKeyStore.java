package tech.kayys.andalus.anp.identity;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory thread-safe implementation of {@link AnpKeyStore}.
 *
 * <p>Revocation is terminal for a key identifier. Register a new identifier when rotating
 * keys; attempting to store a previously revoked identifier is rejected.
 */
public final class InMemoryAnpKeyStore implements AnpKeyStore {

    private final Map<String, AnpIdentityKeyPair> keysById = new ConcurrentHashMap<>();
    private final Map<String, String> keyIdByAgent = new ConcurrentHashMap<>();
    private final Set<String> revokedKeyIds = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized Optional<AnpIdentityKeyPair> getKey(String keyId) {
        if (keyId == null || keyId.isBlank() || revokedKeyIds.contains(keyId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(keysById.get(keyId));
    }

    /** A lookup ordered after revokeKey completes cannot obtain the revoked key. */
    @Override
    public synchronized Optional<AnpIdentityKeyPair> getActiveKey(String keyId) {
        if (keyId == null || keyId.isBlank() || revokedKeyIds.contains(keyId)) {
            return Optional.empty();
        }
        return Optional.ofNullable(keysById.get(keyId));
    }

    @Override
    public synchronized void revokeKey(String keyId) {
        if (keyId == null || keyId.isBlank()) {
            throw new IllegalArgumentException("keyId must not be blank");
        }
        revokedKeyIds.add(keyId);
        keyIdByAgent.entrySet().removeIf(entry -> entry.getValue().equals(keyId));
    }

    @Override
    public Optional<String> getAgentKeyId(String agentId) {
        return Optional.ofNullable(keyIdByAgent.get(agentId));
    }

    @Override
    public synchronized void storeKey(String agentId, AnpIdentityKeyPair keyPair) {
        if (agentId == null || agentId.isBlank()) {
            throw new IllegalArgumentException("agentId must not be blank");
        }
        if (keyPair == null) {
            throw new NullPointerException("keyPair");
        }
        if (revokedKeyIds.contains(keyPair.keyId())) {
            throw new IllegalStateException("A revoked ANP key identifier cannot be reactivated: " + keyPair.keyId());
        }
        keysById.put(keyPair.keyId(), keyPair);
        keyIdByAgent.put(agentId, keyPair.keyId());
    }
}
