package tech.kayys.andalus.anp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.anp.auth.AnpAuthenticator;
import tech.kayys.andalus.anp.auth.AnpAuthVerifier;
import tech.kayys.andalus.anp.auth.AnpHttpSignature;
import tech.kayys.andalus.anp.identity.AnpIdentityKeyPair;
import tech.kayys.andalus.anp.identity.InMemoryAnpKeyStore;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AnpHttpSignatureTest {

    private InMemoryAnpKeyStore keyStore;
    private AnpAuthenticator authenticator;
    private AnpAuthVerifier verifier;
    private AnpIdentityKeyPair keyPair;

    @BeforeEach
    void setUp() {
        keyStore = new InMemoryAnpKeyStore();
        authenticator = new AnpAuthenticator();
        verifier = new AnpAuthVerifier(keyStore);

        byte[] secret = "test-secret-key-material-32bytes!".getBytes(StandardCharsets.UTF_8);
        keyPair = new AnpIdentityKeyPair(
                "did:wba:example.com:agent1#key-1",
                "hmac-sha256",
                secret,
                secret
        );
        keyStore.storeKey("agent1", keyPair);
    }

    @Test
    void signsAndVerifiesValidRequest() {
        AnpHttpSignature sig = authenticator.sign("POST", "/message", "example.com", keyPair);
        assertNotNull(sig);
        assertEquals("did:wba:example.com:agent1#key-1", sig.keyId());
        assertTrue(verifier.verify("POST", "/message", "example.com", sig));
    }

    @Test
    void rejectsTamperedPathOrMethod() {
        AnpHttpSignature sig = authenticator.sign("POST", "/message", "example.com", keyPair);
        assertFalse(verifier.verify("GET", "/message", "example.com", sig));
        assertFalse(verifier.verify("POST", "/tampered", "example.com", sig));
        assertFalse(verifier.verify("POST", "/message", "otherhost.com", sig));
    }

    @Test
    void revokedKeyCannotAuthenticateAnAlreadySignedRequest() {
        AnpHttpSignature sig = authenticator.sign("POST", "/message", "example.com", keyPair);
        keyStore.revokeKey(keyPair.keyId());

        assertFalse(verifier.verify("POST", "/message", "example.com", sig));
        assertTrue(keyStore.getKey(keyPair.keyId()).isPresent(),
                "Revocation need not delete retained key material");
        assertTrue(keyStore.getActiveKey(keyPair.keyId()).isEmpty());
        assertTrue(keyStore.getAgentKeyId("agent1").isEmpty());
    }

    @Test
    void revokedKeyIdCannotBeReactivatedButRotationWithNewIdWorks() {
        keyStore.revokeKey(keyPair.keyId());
        assertThrows(IllegalStateException.class, () -> keyStore.storeKey("agent1", keyPair));

        byte[] rotatedSecret = "rotated-test-secret-material-32b".getBytes(StandardCharsets.UTF_8);
        AnpIdentityKeyPair rotated = new AnpIdentityKeyPair(
                "did:wba:example.com:agent1#key-2",
                "hmac-sha256",
                rotatedSecret,
                rotatedSecret
        );
        keyStore.storeKey("agent1", rotated);

        assertEquals(rotated.keyId(), keyStore.getAgentKeyId("agent1").orElseThrow());
        AnpHttpSignature rotatedSignature =
                authenticator.sign("POST", "/message", "example.com", rotated);
        assertTrue(verifier.verify("POST", "/message", "example.com", rotatedSignature));
    }

    @Test
    void signatureHeaderRoundTrip() {
        AnpHttpSignature sig = authenticator.sign("POST", "/message", "example.com", keyPair);
        String header = sig.toHeaderValue();
        AnpHttpSignature parsed = AnpHttpSignature.parseHeader(header);
        assertEquals(sig.keyId(), parsed.keyId());
        assertEquals(sig.algorithm(), parsed.algorithm());
        assertArrayEquals(sig.signatureBytes(), parsed.signatureBytes());
    }
}
