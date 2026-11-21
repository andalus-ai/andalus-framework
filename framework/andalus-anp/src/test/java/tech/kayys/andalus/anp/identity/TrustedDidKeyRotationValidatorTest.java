package tech.kayys.andalus.anp.identity;
import org.junit.jupiter.api.Test;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
class TrustedDidKeyRotationValidatorTest {
    private static final String DID = "did:wba:agents.example.com:finance";
    private static final String KEY_ID = DID + "#auth-next";
    @Test void acceptsOnlyTheKeyAuthorizedByCandidateDocument() throws Exception {
        KeyPair pair = generate();
        assertDoesNotThrow(() -> TrustedDidKeyRotationValidator.validate(candidate(pair, true, 2), pair(pair)));
    }
    @Test void rejectsPublicKeySubstitution() throws Exception {
        KeyPair authorized = generate(), substituted = generate();
        assertThrows(SecurityException.class, () ->
                TrustedDidKeyRotationValidator.validate(candidate(authorized, true, 2), pair(substituted)));
    }
    @Test void rejectsKeyNotAuthorizedForAuthentication() throws Exception {
        KeyPair key = generate();
        assertThrows(SecurityException.class, () ->
                TrustedDidKeyRotationValidator.validate(candidate(key, false, 2), pair(key)));
    }
    @Test void rejectsIncompleteOrNonEd25519KeyPair() throws Exception {
        KeyPair key = generate();
        AnpIdentityKeyPair incomplete = new AnpIdentityKeyPair(KEY_ID, "Ed25519", null, key.getPublic().getEncoded());
        assertThrows(SecurityException.class, () ->
                TrustedDidKeyRotationValidator.validate(candidate(key, true, 2), incomplete));
    }
    private static KeyPair generate() throws Exception {
        return KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
    }
    private static TrustedDidDocument candidate(KeyPair key, boolean authorize, long revision) {
        byte[] encoded = key.getPublic().getEncoded();
        byte[] raw = Arrays.copyOfRange(encoded, encoded.length - 32, encoded.length);
        Map<String,Object> jwk = Map.of("kty","OKP","crv","Ed25519",
                "x", Base64.getUrlEncoder().withoutPadding().encodeToString(raw));
        DidWbaDocument doc = new DidWbaDocument(DID,
                List.of(DidWbaVerificationMethod.jwk(KEY_ID, DID, jwk)),
                authorize ? List.of("#auth-next") : List.of(), Map.of());
        return new TrustedDidDocument(doc, revision);
    }
    private static AnpIdentityKeyPair pair(KeyPair key) {
        return new AnpIdentityKeyPair(KEY_ID, "Ed25519",
                key.getPrivate().getEncoded(), key.getPublic().getEncoded());
    }
}
