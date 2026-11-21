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
        assertEquals(1, resolver.findRegisteredVersioned(DidWbaIdentity.parse(DID)).orElseThrow().revision());
        assertTrue(document.isAuthenticationMethod("#key-1"));
        assertTrue(document.findVerificationMethod(DID + "#key-1").isPresent());
    }

    @Test
    void acceptsMonotonicRevisionAndExposesCurrentTrustedSnapshot() {
        DidWbaResolver resolver = new DidWbaResolver();
        DidWbaDocument first = validDocument();
        DidWbaDocument second = documentWithKey("key-2");

        resolver.register(first, 4);
        resolver.register(second, 5);

        TrustedDidDocument current = resolver.findRegisteredVersioned(DidWbaIdentity.parse(DID)).orElseThrow();
        assertEquals(5, current.revision());
        assertEquals(second, current.document());
    }

    @Test
    void rejectsStaleRevisionWithoutReplacingCurrentDocument() {
        DidWbaResolver resolver = new DidWbaResolver();
        DidWbaDocument current = documentWithKey("key-2");
        resolver.register(current, 8);

        assertThrows(SecurityException.class, () -> resolver.register(validDocument(), 7));
        assertEquals(current, resolver.findRegistered(DidWbaIdentity.parse(DID)).orElseThrow());
        assertEquals(8, resolver.findRegisteredVersioned(DidWbaIdentity.parse(DID)).orElseThrow().revision());
    }

    @Test
    void rejectsDifferentContentAtSameRevision() {
        DidWbaResolver resolver = new DidWbaResolver();
        resolver.register(validDocument(), 3);

        assertThrows(SecurityException.class, () -> resolver.register(documentWithKey("key-2"), 3));
        assertEquals(validDocument(), resolver.findRegistered(DidWbaIdentity.parse(DID)).orElseThrow());
    }

    @Test
    void acceptsIdempotentReplayAtSameRevision() {
        DidWbaResolver resolver = new DidWbaResolver();
        DidWbaDocument document = validDocument();

        resolver.register(document, 3);
        resolver.register(document, 3);

        assertEquals(3, resolver.findRegisteredVersioned(DidWbaIdentity.parse(DID)).orElseThrow().revision());
    }

    @Test
    void rejectsNonPositiveRevision() {
        assertThrows(IllegalArgumentException.class, () -> new DidWbaResolver().register(validDocument(), 0));
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

    @Test
    void separateResolversShareAtomicRevisionStateThroughStore() {
        InMemoryTrustedDidDocumentStore sharedStore = new InMemoryTrustedDidDocumentStore();
        DidWbaResolver firstNode = new DidWbaResolver(sharedStore);
        DidWbaResolver secondNode = new DidWbaResolver(sharedStore);
        DidWbaDocument newer = documentWithKey("key-2");

        firstNode.register(newer, 9);

        assertThrows(SecurityException.class, () -> secondNode.register(validDocument(), 8));
        assertEquals(newer, secondNode.findRegistered(DidWbaIdentity.parse(DID)).orElseThrow());
    }

    private static DidWbaDocument validDocument() {
        return documentWithKey("key-1");
    }

    private static DidWbaDocument documentWithKey(String keyId) {
        return new DidWbaDocument(DID,
                List.of(DidWbaVerificationMethod.ed25519(DID + "#" + keyId, DID, "zKey-" + keyId)),
                List.of("#" + keyId), Map.of());
    }
}
