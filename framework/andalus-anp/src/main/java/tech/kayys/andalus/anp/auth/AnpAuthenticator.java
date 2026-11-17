package tech.kayys.andalus.anp.auth;

import tech.kayys.andalus.anp.identity.AnpIdentityKeyPair;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Locale;
import java.util.Objects;

/**
 * Signs outbound ANP HTTP requests using an explicitly supported asymmetric algorithm.
 *
 * <p>Ed25519 private keys must be encoded as PKCS#8. No fallback algorithm is attempted.
 */
public final class AnpAuthenticator {

    public AnpHttpSignature sign(
            String method,
            String path,
            String host,
            AnpIdentityKeyPair keyPair) {

        Objects.requireNonNull(keyPair, "keyPair");
        if (!keyPair.canSign()) {
            throw new IllegalStateException("Key " + keyPair.keyId() + " has no private key material");
        }

        String canonical = buildCanonicalString(method, path, host);
        byte[] signature = computeSignature(canonical.getBytes(StandardCharsets.UTF_8), keyPair);

        return new AnpHttpSignature(
                keyPair.keyId(),
                normalizeAlgorithm(keyPair.algorithm()),
                "(request-target) host",
                signature
        );
    }

    public static String buildCanonicalString(String method, String path, String host) {
        return "(request-target): " + method.toLowerCase(Locale.ROOT) + " " + path + "\nhost: " + host;
    }

    static String normalizeAlgorithm(String algorithm) {
        if (algorithm == null || !"ed25519".equalsIgnoreCase(algorithm)) {
            throw new IllegalArgumentException("Unsupported ANP signature algorithm: " + algorithm);
        }
        return "ed25519";
    }

    private byte[] computeSignature(byte[] data, AnpIdentityKeyPair keyPair) {
        try {
            String algorithm = normalizeAlgorithm(keyPair.algorithm());
            PrivateKey privateKey = KeyFactory.getInstance("Ed25519")
                    .generatePrivate(new PKCS8EncodedKeySpec(keyPair.privateKeyBytes()));
            Signature signer = Signature.getInstance(algorithm);
            signer.initSign(privateKey);
            signer.update(data);
            return signer.sign();
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to create Ed25519 ANP signature", e);
        }
    }
}
