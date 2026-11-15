package tech.kayys.andalus.anp.identity;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DidWbaResolverTest {

    private static final String DID = "did:wba:agents.example.com:finance";

    @Test
    void registersConsistentDocumentAndResolvesIt() {
        DidWbaDocument document = validDocument();
        DidWbaResolver resolver = new DidWbaResolver();

        resolver.register(document);

        assertEquals(document, resolver.resolve(DidWbaIdentity.parse(DID)).toCompletableFuture().join().orElseThrow());
        assertTrue(document.isAuthenticationMethod("#key-1"));
        assertTrue(document.findVerificationMethod(DID + "#key-1").isPresent());
    }

    @Test
    void rejectsDocumentWhoseSubjectDoesNotMatchRequestedDidAtRegistrationBoundary() {
        DidWbaResolver resolver = new DidWbaResolver();

        assertThrows(IllegalArgumentException.class, () -> resolver.register(
                new DidWbaDocument("did:wba:attacker.example.com",
                        List.of(DidWbaVerificationMethod.ed25519(
                                "did:wba:attacker.example.com#key-1",
                                "did:wba:attacker.example.com", "zKey")),
                        List.of("#key-1"), Map.of())));
    }

    @Test
    void rejectsVerificationMethodControlledByAnotherDid() {
        DidWbaDocument document = new DidWbaDocument(DID,
                List.of(DidWbaVerificationMethod.ed25519(DID + "#key-1",
                        "did:wba:attacker.example.com", "zKey")),
                List.of("#key-1"), Map.of());

        assertThrows(IllegalArgumentException.class, () -> new DidWbaResolver().register(document));
    }

    @Test
    void rejectsAuthenticationReferenceToUnknownMethod() {
        DidWbaDocument document = new DidWbaDocument(DID,
                List.of(DidWbaVerificationMethod.ed25519(DID + "#key-1", DID, "zKey")),
                List.of("#attacker-key"), Map.of());

        assertThrows(IllegalArgumentException.class, () -> new DidWbaResolver().register(document));
    }

    @Test
    void doesNotResolveAmbiguousSuffixMatches() {
        DidWbaDocument document = validDocument();

        assertTrue(document.findVerificationMethod("#key-1").isPresent());
        assertTrue(document.findVerificationMethod("prefix-key-1").isEmpty());
    }

    private static DidWbaDocument validDocument() {
        return new DidWbaDocument(DID,
                List.of(DidWbaVerificationMethod.ed25519(DID + "#key-1", DID, "zKey")),
                List.of("#key-1"), Map.of());
    }
}
