package tech.kayys.andalus.anp.identity;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class DidWbaRemoteResolverTest {
    private static final String DID = "did:wba:agents.example.com:finance";

    @Test
    void fetchesRemoteDocumentWithConfiguredBoundsAndHttpsUrl() {
        AtomicReference<URI> uri = new AtomicReference<>();
        AtomicReference<Duration> timeout = new AtomicReference<>();
        AtomicReference<Integer> maxBytes = new AtomicReference<>();
        DidWbaResolver resolver = new DidWbaResolver((url, limit, bytes) -> {
            uri.set(url); timeout.set(limit); maxBytes.set(bytes);
            return CompletableFuture.completedFuture(Optional.of(validDocument()));
        }, Duration.ofSeconds(2), 4096);

        assertTrue(resolver.resolve(DidWbaIdentity.parse(DID)).toCompletableFuture().join().isPresent());
        assertEquals(URI.create("https://agents.example.com/finance/did.json"), uri.get());
        assertEquals(Duration.ofSeconds(2), timeout.get());
        assertEquals(4096, maxBytes.get());
    }

    @Test
    void rejectsDocumentWhoseSubjectDiffersFromRequestedDid() {
        DidWbaDocument attacker = new DidWbaDocument("did:wba:attacker.example.com",
                List.of(), List.of(), Map.of());
        DidWbaResolver resolver = new DidWbaResolver((url, timeout, bytes) ->
                CompletableFuture.completedFuture(Optional.of(attacker)), Duration.ofSeconds(1), 1024);

        assertThrows(java.util.concurrent.CompletionException.class,
                () -> resolver.resolve(DidWbaIdentity.parse(DID)).toCompletableFuture().join());
    }

    @Test
    void returnsEmptyWhenFetcherReportsNotFound() {
        DidWbaResolver resolver = new DidWbaResolver((url, timeout, bytes) ->
                CompletableFuture.completedFuture(Optional.empty()), Duration.ofSeconds(1), 1024);
        assertTrue(resolver.resolve(DidWbaIdentity.parse(DID)).toCompletableFuture().join().isEmpty());
    }

    @Test
    void localRegistryPreventsRemoteFetch() {
        DidWbaResolver resolver = new DidWbaResolver((url, timeout, bytes) -> {
            fail("fetcher should not be called");
            return CompletableFuture.completedFuture(Optional.empty());
        }, Duration.ofSeconds(1), 1024);
        DidWbaDocument document = validDocument();
        resolver.register(document);
        assertEquals(document, resolver.resolve(DidWbaIdentity.parse(DID)).toCompletableFuture().join().orElseThrow());
    }

    @Test
    void requiresPositiveTimeoutAndResponseLimit() {
        assertThrows(IllegalArgumentException.class,
                () -> new DidWbaResolver((url, timeout, bytes) -> null, Duration.ZERO, 100));
        assertThrows(IllegalArgumentException.class,
                () -> new DidWbaResolver((url, timeout, bytes) -> null, Duration.ofSeconds(1), 0));
    }

    private static DidWbaDocument validDocument() {
        return new DidWbaDocument(DID,
                List.of(DidWbaVerificationMethod.ed25519(DID + "#key-1", DID, "zKey")),
                List.of("#key-1"), Map.of());
    }
}
