package tech.kayys.andalus.anp.identity;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

/**
 * Resolves an authentication-authorized Ed25519 public key from a DID:WBA document.
 *
 * <p>The supplied document must come from a trusted registration/resolution boundary.
 * Structural validation and HTTPS retrieval alone do not establish document provenance.
 */
public final class DidWbaPublicKeyResolver {
    private static final byte[] ED25519_X509_PREFIX = hex("302a300506032b6570032100");
    private static final byte[] ED25519_MULTICODEC_PREFIX = new byte[] {(byte) 0xed, 0x01};

    private DidWbaPublicKeyResolver() {
    }

    /**
     * The key ID is the exact DID verification-method URL (or its exact fragment).
     * A method not listed in the document's authentication relationship is rejected.
     */
    public static PublicKey resolveAuthenticationKey(DidWbaDocument document, String keyId) {
        Objects.requireNonNull(document, "document");
        if (keyId == null || keyId.isBlank() || !document.isAuthenticationMethod(keyId)) {
            throw new SecurityException("DID key is not authorized for authentication");
        }

        DidWbaVerificationMethod method = document.findVerificationMethod(keyId)
                .orElseThrow(() -> new SecurityException("DID verification method was not found"));
        if (!document.id().equals(method.controller())) {
            throw new SecurityException("DID verification method controller mismatch");
        }

        byte[] rawKey;
        if ("Ed25519VerificationKey2020".equals(method.type())) {
            rawKey = decodeMultibaseEd25519(method.publicKeyMultibase());
        } else if ("JsonWebKey2020".equals(method.type())) {
            rawKey = decodeEd25519Jwk(method.publicKeyJwk());
        } else {
            throw new SecurityException("Unsupported DID verification method type");
        }

        if (rawKey.length != 32) {
            throw new SecurityException("Ed25519 public key must contain exactly 32 bytes");
        }
        try {
            byte[] encoded = Arrays.copyOf(ED25519_X509_PREFIX, ED25519_X509_PREFIX.length + rawKey.length);
            System.arraycopy(rawKey, 0, encoded, ED25519_X509_PREFIX.length, rawKey.length);
            return KeyFactory.getInstance("Ed25519").generatePublic(new X509EncodedKeySpec(encoded));
        } catch (Exception exception) {
            throw new SecurityException("Invalid Ed25519 public key", exception);
        }
    }

    private static byte[] decodeEd25519Jwk(java.util.Map<String, Object> jwk) {
        if (!"OKP".equals(jwk.get("kty")) || !"Ed25519".equals(jwk.get("crv"))
                || jwk.containsKey("d") || !(jwk.get("x") instanceof String x)) {
            throw new SecurityException("DID JWK must be an Ed25519 OKP public key");
        }
        try {
            return Base64.getUrlDecoder().decode(x);
        } catch (IllegalArgumentException exception) {
            throw new SecurityException("Malformed Ed25519 JWK x coordinate", exception);
        }
    }

    private static byte[] decodeMultibaseEd25519(String value) {
        if (value == null || value.length() < 2 || value.charAt(0) != 'z') {
            throw new SecurityException("Ed25519 publicKeyMultibase must use base58btc");
        }
        byte[] decoded = decodeBase58(value.substring(1));
        if (decoded.length != 34 || decoded[0] != ED25519_MULTICODEC_PREFIX[0]
                || decoded[1] != ED25519_MULTICODEC_PREFIX[1]) {
            throw new SecurityException("publicKeyMultibase must contain the Ed25519 multicodec prefix and 32-byte key");
        }
        return Arrays.copyOfRange(decoded, 2, decoded.length);
    }

    private static byte[] decodeBase58(String value) {
        final String alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        BigInteger number = BigInteger.ZERO;
        for (int i = 0; i < value.length(); i++) {
            int digit = alphabet.indexOf(value.charAt(i));
            if (digit < 0) {
                throw new SecurityException("Malformed base58btc public key");
            }
            number = number.multiply(BigInteger.valueOf(58)).add(BigInteger.valueOf(digit));
        }
        byte[] magnitude = number.signum() == 0 ? new byte[0] : number.toByteArray();
        if (magnitude.length > 0 && magnitude[0] == 0) {
            magnitude = Arrays.copyOfRange(magnitude, 1, magnitude.length);
        }
        int leadingZeros = 0;
        while (leadingZeros < value.length() && value.charAt(leadingZeros) == '1') {
            leadingZeros++;
        }
        byte[] decoded = new byte[leadingZeros + magnitude.length];
        System.arraycopy(magnitude, 0, decoded, leadingZeros, magnitude.length);
        return decoded;
    }

    private static byte[] hex(String value) {
        byte[] bytes = new byte[value.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(value.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }
}
