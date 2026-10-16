package tech.kayys.andalus.knowledge.streaming;

import tech.kayys.andalus.knowledge.*;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class KnowledgeStreamingTest {

    static class TestRegistry implements KnowledgeRegistry {
        private final Map<String, KnowledgeSource> sources = new LinkedHashMap<>();
        @Override public void register(KnowledgeSource s) { sources.put(s.id(), s); }
        @Override public void unregister(String id) { sources.remove(id); }
        @Override public Optional<KnowledgeSource> find(String id) { return Optional.ofNullable(sources.get(id)); }
        @Override public List<KnowledgeSource> sources() { return List.copyOf(sources.values()); }
    }

    static final KnowledgeSelector PASSTHROUGH = (c, b, ctx) ->
            new KnowledgeSelectionResult(c, List.of(), List.of(), 0, Map.of());

    static KnowledgeEvidence ev(String id, String srcId) {
        var item = new KnowledgeItem(id, srcId, "fact", "t", "c", Map.of(), null, null, 
                new KnowledgeValidity(Instant.EPOCH, null, "ACTIVE"));
        return KnowledgeEvidence.of(item, 0.9, "test");
    }

    @Test void streamEmitsEvidenceFromMultipleSources() throws Exception {
        var registry = new TestRegistry();
        registry.register(new KnowledgeSource() {
            @Override public String id() { return "s1"; }
            @Override public CompletionStage<KnowledgeResult> query(KnowledgeQuery q, KnowledgeContext ctx) {
                return CompletableFuture.completedFuture(KnowledgeResult.of(q, List.of(ev("e1", "s1"))));
            }
        });
        registry.register(new KnowledgeSource() {
            @Override public String id() { return "s2"; }
            @Override public CompletionStage<KnowledgeResult> query(KnowledgeQuery q, KnowledgeContext ctx) {
                return CompletableFuture.completedFuture(KnowledgeResult.of(q, List.of(ev("e2", "s2"))));
            }
        });

        var resolver = new KnowledgeEventStreamingResolver(registry, null, PASSTHROUGH, null, null);
        var query = KnowledgeQuery.of("test");

        List<KnowledgeEvidence> received = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        resolver.stream(query, KnowledgeContext.empty()).subscribe(
                new Flow.Subscriber<>() {
                    Flow.Subscription sub;
                    @Override public void onSubscribe(Flow.Subscription s) { sub = s; s.request(Long.MAX_VALUE); }
                    @Override public void onNext(KnowledgeEvidence e) { received.add(e); }
                    @Override public void onError(Throwable t) { latch.countDown(); }
                    @Override public void onComplete() { latch.countDown(); }
                }
        );

        assertTrue(latch.await(5, TimeUnit.SECONDS), "stream should complete");
        assertEquals(2, received.size());
    }

    @Test void streamingResolverBackwardCompatibleResolve() throws Exception {
        var registry = new TestRegistry();
        registry.register(new KnowledgeSource() {
            @Override public String id() { return "s1"; }
            @Override public CompletionStage<KnowledgeResult> query(KnowledgeQuery q, KnowledgeContext ctx) {
                return CompletableFuture.completedFuture(KnowledgeResult.of(q, List.of(ev("e1", "s1"))));
            }
        });

        var resolver = new KnowledgeEventStreamingResolver(registry, null, PASSTHROUGH, null, null);
        // must work as a standard KnowledgeResolver
        KnowledgeResolver standard = resolver;
        var result = standard.resolve(KnowledgeQuery.of("test"), KnowledgeContext.empty())
                .toCompletableFuture().get(5, TimeUnit.SECONDS);
        assertEquals(1, result.evidence().size());
    }

    @Test void knowledgeStreamingEventTypes() {
        var ev = new KnowledgeStreamingEvent.EvidenceArrived("src1", ev("e1", "s1"), Instant.now());
        assertEquals("src1", ev.sourceId());

        var completed = new KnowledgeStreamingEvent.ResolutionCompleted(5, 3, 120, false, Instant.now());
        assertEquals(5, completed.totalEvidence());
        assertFalse(completed.partial());
    }
}
