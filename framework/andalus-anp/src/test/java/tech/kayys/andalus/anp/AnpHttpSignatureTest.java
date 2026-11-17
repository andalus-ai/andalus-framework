package tech.kayys.andalus.anp;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.anp.auth.AnpAuthenticator;
import tech.kayys.andalus.anp.auth.AnpAuthVerifier;
import tech.kayys.andalus.anp.auth.AnpHttpSignature;
import tech.kayys.andalus.anp.identity.AnpIdentityKeyPair;
import tech.kayys.andalus.anp.identity.InMemoryAnpKeyStore;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;

import static org.junit.jupiter.api.Assertions.*;

class AnpHttpSignatureTest {

    private InMemoryAnpKeyStore keyStore;
    private AnpAuthenticator authenticator;
    private AnpAuthVerifier verifier;
    private AnpIdentityKeyPair keyPair;

    @BeforeEach
    void setUp() throws Exception {
        keyStore = new InMemoryAnpKeyStore();
        authenticator = new AnpAuthenticator();
        verifier = new AnpAuthVerifier(keyStore);

        KeyPair generated = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        keyPair = new AnpIdentityKeyPair(
                "did:wba:example.com:agent1#key-1",
                "Ed25519",
                generated.getPrivate().getEncoded(),
                generated.getPublic().getEncoded()
        );
        keyStore.storeKey("agent1", keyPair);
    }

    @Test
    void signsAndVerifiesValidRequest() {
        AnpHttpSignature sig = authenticator.sign("POST", "/message", "example.com", keyPair);
        assertNotNull(sig);
        assertEquals(keyPair.keyId(), sig.keyId());
        assertEquals("ed25519", sig.algorithm());
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
    void rejectsAlgorithmConfusionAndUnknownAlgorithms() {
        AnpHttpSignature valid = authenticator.sign("POST", "/message", "example.com", keyPair);
        AnpHttpSignature mismatch = new AnpHttpSignature(
                valid.keyId(), "hmac-sha256", valid.signedHeaders(), valid.signatureBytes());
        AnpHttpSignature unknown = new AnpHttpSignature(
                valid.keyId(), "ed25519+unknown", valid.signedHeaders(), valid.signatureBytes());

        assertFalse(verifier.verify("POST", "/message", "example.com", mismatch));
        assertFalse(verifier.verify("POST", "/message", "example.com", unknown));
        assertThrows(IllegalArgumentException.class,
                () -> authenticator.sign("POST", "/", "example.com",
                        new AnpIdentityKeyPair("legacy", "hmac-sha256", new byte[]{1}, new byte[]{1})));
    }

    @Test
    void rejectsForgedSignature() throws Exception {
        AnpHttpSignature valid = authenticator.sign("POST", "/message", "example.com", keyPair);
        KeyPair attacker = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();
        Signature signer = Signature.getInstance("Ed25519");
        signer.initSign(attacker.getPrivate());
        signer.update(AnpAuthenticator.buildCanonicalString("POST", "/message", "example.com")
                .getBytes(java.nio.charset.StandardCharsets.UTF_8));
        byte[] forged = signer.sign();

        AnpHttpSignature forgedSignature =
                new AnpHttpSignature(valid.keyId(), "ed25519", valid.signedHeaders(), forged);
        assertFalse(verifier.verify("POST", "/message", "example.com", forgedSignature));
    }

    @Test
    void revokedKeyCannotAuthenticateAnAlreadySignedRequest() {
        AnpHttpSignature sig = authenticator.sign("POST", "/message", "example.com", keyPair);
        keyStore.revokeKey(keyPair.keyId());
        assertFalse(verifier.verify("POST", "/message", "example.com", sig));
    }

    @Test
    void signatureHeaderRoundTrip() {
        AnpHttpSignature sig = authenticator.sign("POST", "/message", "example.com", keyPair);
        AnpHttpSignature parsed = AnpHttpSignature.parseHeader(sig.toHeaderValue());
        assertEquals(sig.keyId(), parsed.keyId());
        assertEquals(sig.algorithm(), parsed.algorithm());
        assertArrayEquals(sig.signatureBytes(), parsed.signatureBytes());
    }
}
