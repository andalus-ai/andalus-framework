package tech.kayys.andalus.anp.auth;

import tech.kayys.andalus.anp.identity.AnpIdentityKeyPair;
import tech.kayys.andalus.anp.identity.AnpKeyStore;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.Optional;

/** Verifies inbound ANP HTTP signatures against active registered Ed25519 public keys. */
public final class AnpAuthVerifier {

    private final AnpKeyStore keyStore;
    private final AnpDidKeyResolver didKeyResolver;

    /**
     * Compatibility constructor for deployments that have not enabled DID-document
     * key binding. Production inbound authentication should use the resolver overload.
     */
    public AnpAuthVerifier(AnpKeyStore keyStore) {
        this(keyStore, null);
    }

    /**
     * Creates a verifier that requires the active key-store key to match the key
     * authorized by a trusted DID document.
     */
    public AnpAuthVerifier(AnpKeyStore keyStore, AnpDidKeyResolver didKeyResolver) {
        this.keyStore = Objects.requireNonNull(keyStore, "keyStore");
        this.didKeyResolver = didKeyResolver;
    }

    /**
     * Verifies an incoming request. Unknown algorithms, algorithm/key mismatches,
     * revoked keys, and malformed public-key encodings are rejected.
     */
    public boolean verify(
            String method,
            String path,
            String host,
            AnpHttpSignature signature) {

        Objects.requireNonNull(signature, "signature");
        Optional<AnpIdentityKeyPair> keyOpt = keyStore.getActiveKey(signature.keyId());
        if (keyOpt.isEmpty()) {
            return false;
        }

        AnpIdentityKeyPair key = keyOpt.get();
        final String algorithm;
        try {
            algorithm = AnpAuthenticator.normalizeAlgorithm(signature.algorithm());
            if (!algorithm.equalsIgnoreCase(key.algorithm())) {
                return false;
            }
            if (key.publicKeyBytes() == null || key.publicKeyBytes().length == 0) {
                return false;
            }

            PublicKey publicKey = KeyFactory.getInstance("Ed25519")
                    .generatePublic(new X509EncodedKeySpec(key.publicKeyBytes()));
            if (didKeyResolver != null) {
                Optional<PublicKey> didKey = didKeyResolver.resolveAuthenticationKey(signature.keyId());
                if (didKey.isEmpty()
                        || !MessageDigest.isEqual(publicKey.getEncoded(), didKey.get().getEncoded())) {
                    return false;
                }
            }
            Signature verifier = Signature.getInstance(algorithm);
            verifier.initVerify(publicKey);
            verifier.update(AnpAuthenticator.buildCanonicalString(method, path, host)
                    .getBytes(StandardCharsets.UTF_8));
            return verifier.verify(signature.signatureBytes());
        } catch (Exception e) {
            return false;
        }
    }
}
