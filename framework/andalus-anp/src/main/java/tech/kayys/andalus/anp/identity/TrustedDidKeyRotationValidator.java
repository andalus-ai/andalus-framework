package tech.kayys.andalus.anp.identity;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.util.Objects;
/** Validates the DID-to-signing-key binding before a rotation is committed. */
public final class TrustedDidKeyRotationValidator {
    private TrustedDidKeyRotationValidator() {}
    /**
     * Checks that the candidate trusted DID snapshot authorizes the exact key ID
     * and exposes the same Ed25519 public key as the signing key pair.
     * This is a precondition check, not a cross-store transaction coordinator.
     */
    public static void validate(TrustedDidDocument candidate, AnpIdentityKeyPair newKeyPair) {
        Objects.requireNonNull(candidate, "candidate");
        Objects.requireNonNull(newKeyPair, "newKeyPair");
        if (!"Ed25519".equalsIgnoreCase(newKeyPair.algorithm()) || !newKeyPair.canSign()
                || newKeyPair.publicKeyBytes() == null || newKeyPair.publicKeyBytes().length == 0) {
            throw new SecurityException("A rotation requires a complete Ed25519 signing key pair");
        }
        PublicKey authorized = DidWbaPublicKeyResolver.resolveAuthenticationKey(
                candidate.document(), newKeyPair.keyId());
        if (!MessageDigest.isEqual(authorized.getEncoded(), newKeyPair.publicKeyBytes())) {
            throw new SecurityException("Proposed DID document does not authorize the supplied signing key");
        }
    }
}
