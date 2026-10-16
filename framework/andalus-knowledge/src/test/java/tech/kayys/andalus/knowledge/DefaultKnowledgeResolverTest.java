package tech.kayys.andalus.knowledge;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class DefaultKnowledgeResolverTest {

    // Simple in-memory registry for tests
    static class TestRegistry implements KnowledgeRegistry {
        private final Map<String, KnowledgeSource> sources = new LinkedHashMap<>();
        @Override public void register(KnowledgeSource s) { sources.put(s.id(), s); }
        @Override public void unregister(String id) { sources.remove(id); }
        @Override public Optional<KnowledgeSource> find(String id) { return Optional.ofNullable(sources.get(id)); }
        @Override public List<KnowledgeSource> sources() { return List.copyOf(sources.values()); }
    }

    // Simple pass-through selector (returns all candidates)
    static final KnowledgeSelector PASSTHROUGH_SELECTOR = (candidates, budget, ctx) ->
            new KnowledgeSelectionResult(candidates, List.of(), List.of(), 0, Map.of());

    static KnowledgeSource sourceReturning(String id, List<KnowledgeEvidence> evidence) {
        return new KnowledgeSource() {
            @Override public String id() { return id; }
            @Override public CompletionStage<KnowledgeResult> query(KnowledgeQuery q, KnowledgeContext ctx) {
                return CompletableFuture.completedFuture(KnowledgeResult.of(q, evidence));
            }
        };
    }

    static KnowledgeEvidence evidence(String itemId, String sourceId) {
        KnowledgeItem item = new KnowledgeItem(itemId, sourceId, "fact", "title", "content",
                Map.of(), null, null, KnowledgeValidity.active());
        return KnowledgeEvidence.of(item, 0.9, "test");
    }

    @Test void resolvesMergesEvidenceFromMultipleSources() throws Exception {
        var registry = new TestRegistry();
        registry.register(sourceReturning("s1", List.of(evidence("e1", "s1"))));
        registry.register(sourceReturning("s2", List.of(evidence("e2", "s2"))));

        var resolver = new DefaultKnowledgeResolver(registry, PASSTHROUGH_SELECTOR);
        var result = resolver.resolve(KnowledgeQuery.of("test"), KnowledgeContext.empty())
                .toCompletableFuture().get();

        assertEquals(2, result.evidence().size());
    }

    @Test void sourceFailureDoesNotFailWholeResolution() throws Exception {
        var registry = new TestRegistry();
        registry.register(sourceReturning("good", List.of(evidence("e1", "good"))));
        registry.register(new KnowledgeSource() {
            @Override public String id() { return "bad"; }
            @Override public CompletionStage<KnowledgeResult> query(KnowledgeQuery q, KnowledgeContext ctx) {
                return CompletableFuture.failedFuture(new RuntimeException("source down"));
            }
        });

        var resolver = new DefaultKnowledgeResolver(registry, PASSTHROUGH_SELECTOR);
        var result = resolver.resolve(KnowledgeQuery.of("q"), KnowledgeContext.empty())
                .toCompletableFuture().get();

        // got evidence from good source
        assertEquals(1, result.evidence().size());
        // partial flag set
        assertTrue((Boolean) result.metadata().get("resolution.partial"));
    }

    @Test void unhealthySourcesExcluded() throws Exception {
        var registry = new TestRegistry();
        registry.register(new KnowledgeSource() {
            @Override public String id() { return "unhealthy"; }
            @Override public boolean isHealthy() { return false; }
            @Override public CompletionStage<KnowledgeResult> query(KnowledgeQuery q, KnowledgeContext ctx) {
                throw new AssertionError("should not be queried");
            }
        });
        registry.register(sourceReturning("healthy", List.of(evidence("e1", "healthy"))));

        var resolver = new DefaultKnowledgeResolver(registry, PASSTHROUGH_SELECTOR);
        var result = resolver.resolve(KnowledgeQuery.of("q"), KnowledgeContext.empty())
                .toCompletableFuture().get();

        assertEquals(1, result.evidence().size());
    }

    @Test void deniedSourcesExcluded() throws Exception {
        var registry = new TestRegistry();
        registry.register(sourceReturning("denied-src", List.of(evidence("e1", "denied-src"))));

        KnowledgeResolutionPolicy denyAll = (req, src) ->
                CompletableFuture.completedFuture(KnowledgeResolutionDecision.deny("test deny"));

        var resolver = new DefaultKnowledgeResolver(registry, denyAll, PASSTHROUGH_SELECTOR, KnowledgeBudget.defaults());
        var result = resolver.resolve(KnowledgeQuery.of("q"), KnowledgeContext.empty())
                .toCompletableFuture().get();

        assertTrue(result.evidence().isEmpty());
        assertFalse(((List<?>) result.metadata().get("sources.denied")).isEmpty());
    }

    @Test void contextPropagatedToSource() throws Exception {
        var registry = new TestRegistry();
        AtomicReference<KnowledgeContext> captured = new AtomicReference<>();
        registry.register(new KnowledgeSource() {
            @Override public String id() { return "s1"; }
            @Override public CompletionStage<KnowledgeResult> query(KnowledgeQuery q, KnowledgeContext ctx) {
                captured.set(ctx);
                return CompletableFuture.completedFuture(KnowledgeResult.empty(q));
            }
        });

        var ctx = new KnowledgeContext("tenant1", "user1", "agent1", null, null, "default",
                Instant.now(), List.of(), Map.of());
        var resolver = new DefaultKnowledgeResolver(registry, PASSTHROUGH_SELECTOR);
        resolver.resolve(KnowledgeQuery.of("q"), ctx).toCompletableFuture().get();

        assertNotNull(captured.get());
        assertEquals("tenant1", captured.get().tenantId());
    }

    @Test void metadataContainsDiagnosticKeys() throws Exception {
        var registry = new TestRegistry();
        registry.register(sourceReturning("s1", List.of()));

        var resolver = new DefaultKnowledgeResolver(registry, PASSTHROUGH_SELECTOR);
        var result = resolver.resolve(KnowledgeQuery.of("q"), KnowledgeContext.empty())
                .toCompletableFuture().get();

        assertTrue(result.metadata().containsKey("resolution.durationMillis"));
        assertTrue(result.metadata().containsKey("evidence.candidates"));
        assertTrue(result.metadata().containsKey("sources.selected"));
    }

    @Test void invalidItemsFiltered() throws Exception {
        var registry = new TestRegistry();
        var expired = new KnowledgeItem("old", "s1", "fact", "t", "c", Map.of(), null, null,
                KnowledgeValidity.superseded());
        registry.register(new KnowledgeSource() {
            @Override public String id() { return "s1"; }
            @Override public CompletionStage<KnowledgeResult> query(KnowledgeQuery q, KnowledgeContext ctx) {
                return CompletableFuture.completedFuture(
                        KnowledgeResult.of(q, List.of(KnowledgeEvidence.of(expired, 0.9, "test"))));
            }
        });

        var resolver = new DefaultKnowledgeResolver(registry, PASSTHROUGH_SELECTOR);
        var result = resolver.resolve(KnowledgeQuery.of("q"), KnowledgeContext.empty())
                .toCompletableFuture().get();

        assertTrue(result.evidence().isEmpty(), "expired items must be filtered");
    }
}
