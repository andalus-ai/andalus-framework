package tech.kayys.andalus.anp.identity;

import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DidWbaPublicKeyResolverTest {
    private static final String DID = "did:wba:agents.example.com:finance";
    private static final String KEY_ID = DID + "#auth-1";

    @Test
    void resolvesOnlyTheExactAuthenticationMethodAndBindsItsPublicKey() throws Exception {
        KeyPair pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        byte[] raw = java.util.Arrays.copyOfRange(pair.getPublic().getEncoded(),
                pair.getPublic().getEncoded().length - 32, pair.getPublic().getEncoded().length);
        DidWbaDocument document = new DidWbaDocument(DID,
                List.of(DidWbaVerificationMethod.jwk(KEY_ID, DID, Map.of(
                        "kty", "OKP", "crv", "Ed25519",
                        "x", Base64.getUrlEncoder().withoutPadding().encodeToString(raw)))),
                List.of("#auth-1"), Map.of());

        assertArrayEquals(pair.getPublic().getEncoded(),
                DidWbaPublicKeyResolver.resolveAuthenticationKey(document, KEY_ID).getEncoded());
        assertThrows(SecurityException.class,
                () -> DidWbaPublicKeyResolver.resolveAuthenticationKey(document, DID + "#other"));
    }

    @Test
    void rejectsDeclaredButUnauthorizedVerificationMethod() throws Exception {
        KeyPair pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        byte[] raw = java.util.Arrays.copyOfRange(pair.getPublic().getEncoded(),
                pair.getPublic().getEncoded().length - 32, pair.getPublic().getEncoded().length);
        DidWbaDocument document = new DidWbaDocument(DID,
                List.of(DidWbaVerificationMethod.jwk(KEY_ID, DID, Map.of(
                        "kty", "OKP", "crv", "Ed25519",
                        "x", Base64.getUrlEncoder().withoutPadding().encodeToString(raw)))),
                List.of(), Map.of());

        assertThrows(SecurityException.class,
                () -> DidWbaPublicKeyResolver.resolveAuthenticationKey(document, KEY_ID));
    }

    @Test
    void rejectsKeySubstitutionAndUnsupportedKeyTypes() {
        DidWbaDocument document = new DidWbaDocument(DID,
                List.of(new DidWbaVerificationMethod(KEY_ID, "RsaVerificationKey2018",
                        DID, null, Map.of())),
                List.of("#auth-1"), Map.of());

        assertThrows(SecurityException.class,
                () -> DidWbaPublicKeyResolver.resolveAuthenticationKey(document, KEY_ID));
    }

    @Test
    void rejectsMalformedEd25519Jwk() {
        DidWbaDocument document = new DidWbaDocument(DID,
                List.of(DidWbaVerificationMethod.jwk(KEY_ID, DID,
                        Map.of("kty", "OKP", "crv", "X25519", "x", "AA"))),
                List.of("#auth-1"), Map.of());

        assertThrows(SecurityException.class,
                () -> DidWbaPublicKeyResolver.resolveAuthenticationKey(document, KEY_ID));
    }

    @Test
    void rejectsEd25519JwkContainingPrivateKeyMaterial() {
        DidWbaDocument document = new DidWbaDocument(DID,
                List.of(DidWbaVerificationMethod.jwk(KEY_ID, DID,
                        Map.of("kty", "OKP", "crv", "Ed25519", "x", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA",
                                "d", "AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA"))),
                List.of("#auth-1"), Map.of());

        assertThrows(SecurityException.class,
                () -> DidWbaPublicKeyResolver.resolveAuthenticationKey(document, KEY_ID));
    }

    @Test
    void resolvesEd25519Base58BtcMultibaseKey() throws Exception {
        KeyPair pair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        byte[] encoded = pair.getPublic().getEncoded();
        byte[] raw = java.util.Arrays.copyOfRange(encoded, encoded.length - 32, encoded.length);
        byte[] multicodec = new byte[34];
        multicodec[0] = (byte) 0xed;
        multicodec[1] = 0x01;
        System.arraycopy(raw, 0, multicodec, 2, raw.length);
        String multibase = "z" + encodeBase58(multicodec);
        DidWbaDocument document = new DidWbaDocument(DID,
                List.of(DidWbaVerificationMethod.ed25519(KEY_ID, DID, multibase)),
                List.of("#auth-1"), Map.of());

        assertArrayEquals(encoded,
                DidWbaPublicKeyResolver.resolveAuthenticationKey(document, KEY_ID).getEncoded());
    }

    private static String encodeBase58(byte[] input) {
        final String alphabet = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
        java.math.BigInteger value = new java.math.BigInteger(1, input);
        StringBuilder result = new StringBuilder();
        while (value.signum() > 0) {
            java.math.BigInteger[] division = value.divideAndRemainder(java.math.BigInteger.valueOf(58));
            result.append(alphabet.charAt(division[1].intValue()));
            value = division[0];
        }
        for (byte item : input) {
            if (item != 0) break;
            result.append('1');
        }
        return result.reverse().toString();
    }

}
