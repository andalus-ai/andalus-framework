package tech.kayys.andalus.anp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tech.kayys.andalus.anp.auth.AnpAuthenticator;
import tech.kayys.andalus.anp.auth.AnpAuthVerifier;
import tech.kayys.andalus.anp.auth.AnpHttpSignature;
import tech.kayys.andalus.anp.identity.AnpIdentityKeyPair;
import tech.kayys.andalus.anp.identity.DidWbaIdentity;
import tech.kayys.andalus.anp.identity.InMemoryAnpKeyStore;
import tech.kayys.andalus.security.propagation.SecurityContextSnapshot;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AnpSecurityMapperTest {

    private static final String METHOD = "POST";
    private static final String PATH = "/messages";
    private static final String HOST = "example.com";

    private AnpAuthenticator authenticator;
    private AnpSecurityMapper mapper;
    private AnpIdentityKeyPair keyPair;

    @BeforeEach
    void setUp() {
        authenticator = new AnpAuthenticator();
        InMemoryAnpKeyStore keyStore = new InMemoryAnpKeyStore();
        byte[] secret = "test-secret-key-material-32bytes!".getBytes(StandardCharsets.UTF_8);
        keyPair = new AnpIdentityKeyPair(
                "did:wba:example.com:agent1#key-1",
                "hmac-sha256",
                secret,
                secret
        );
        keyStore.storeKey("agent1", keyPair);
        mapper = new AnpSecurityMapper(
                new AnpPrincipalResolver(),
                new AnpAuthVerifier(keyStore)
        );
    }

    @Test
    void createsVerifiedSnapshotOnlyForValidRequestSignature() {
        AnpHttpSignature signature = authenticator.sign(METHOD, PATH, HOST, keyPair);

        SecurityContextSnapshot snapshot = mapper.map(
                DidWbaIdentity.parse("did:wba:example.com:agent1"),
                METHOD,
                PATH,
                HOST,
                signature,
                "tenant-a",
                Optional.empty()
        );

        assertEquals("did:wba:example.com:agent1", snapshot.principal().id());
        assertEquals("tenant-a", snapshot.tenant().tenantId());
        assertEquals(true, snapshot.attributes().get("anp.verified"));
    }

    @Test
    void rejectsInvalidSignatureBeforeCreatingAuthenticatedSnapshot() {
        AnpHttpSignature signature = authenticator.sign(METHOD, PATH, HOST, keyPair);

        assertThrows(SecurityException.class, () -> mapper.map(
                DidWbaIdentity.parse("did:wba:example.com:agent1"),
                "GET",
                PATH,
                HOST,
                signature,
                "tenant-a",
                Optional.empty()
        ));
    }

    @Test
    void rejectsSignatureFromUnknownKey() {
        AnpHttpSignature signature = authenticator.sign(METHOD, PATH, HOST, keyPair);
        AnpHttpSignature unknownKey = new AnpHttpSignature(
                "did:wba:unknown.example:agent#key-1",
                signature.algorithm(),
                signature.signedHeaders(),
                signature.signatureBytes()
        );

        assertThrows(SecurityException.class, () -> mapper.map(
                DidWbaIdentity.parse("did:wba:example.com:agent1"),
                METHOD,
                PATH,
                HOST,
                unknownKey,
                "tenant-a",
                Optional.empty()
        ));
    }
}
