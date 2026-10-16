package tech.kayys.andalus.memory.reactive;

import tech.kayys.andalus.memory.MemoryRecord;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import static org.junit.jupiter.api.Assertions.*;

class ReactiveMemoryTest {

    @Test void storeEmitsStoredEvent() throws Exception {
        var store = new InMemoryReactiveMemoryStore();
        List<MemoryEvent> received = new CopyOnWriteArrayList<>();
        store.addListener(received::add);

        var record = MemoryRecord.of("key1", "value1");
        store.store(record, "test").toCompletableFuture().get(2, TimeUnit.SECONDS);

        assertFalse(received.isEmpty());
        assertInstanceOf(MemoryEvent.MemoryStored.class, received.get(0));
    }

    @Test void recallReturnsMatchingRecords() throws Exception {
        var store = new InMemoryReactiveMemoryStore();
        store.store(new MemoryRecord("r1", "fact", "capital", "Jakarta", Map.of(), null, 0.9, 3600, "s1", "u1"), "test")
                .toCompletableFuture().get();
        store.store(new MemoryRecord("r2", "fact", "currency", "Rupiah", Map.of(), null, 0.8, 3600, "s1", "u1"), "test")
                .toCompletableFuture().get();

        var results = store.recall("u1", "s1", "capital", 10).toCompletableFuture().get();
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.value().contains("Jakarta")));
    }

    @Test void observeTurnEmitsTurnEvent() throws InterruptedException {
        var store = new InMemoryReactiveMemoryStore();
        List<MemoryEvent> events = new CopyOnWriteArrayList<>();
        store.addListener(events::add);
        CountDownLatch latch = new CountDownLatch(1);
        store.addListener(e -> { if (e instanceof MemoryEvent.ConversationTurnObserved) latch.countDown(); });

        store.observeTurn("agent-1", "session-1", "turn-1", "Hello", "Hi there");
        assertTrue(latch.await(2, TimeUnit.SECONDS));

        assertTrue(events.stream().anyMatch(e -> e instanceof MemoryEvent.ConversationTurnObserved));
    }

    @Test void flowPublisherReceivesEvents() throws Exception {
        var store = new InMemoryReactiveMemoryStore();
        List<MemoryEvent> flowReceived = new CopyOnWriteArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        store.events().subscribe(new Flow.Subscriber<>() {
            Flow.Subscription sub;
            @Override public void onSubscribe(Flow.Subscription s) { sub = s; s.request(Long.MAX_VALUE); }
            @Override public void onNext(MemoryEvent e) { flowReceived.add(e); latch.countDown(); }
            @Override public void onError(Throwable t) {}
            @Override public void onComplete() {}
        });

        store.store(MemoryRecord.of("k", "v"), "test");
        assertTrue(latch.await(3, TimeUnit.SECONDS));
        assertFalse(flowReceived.isEmpty());
    }

    @Test void consolidationPolicyAfterNTurns() {
        var policy = MemoryConsolidationPolicy.afterNTurns(3);
        var few = List.of(MemoryRecord.of("k1", "v1"), MemoryRecord.of("k2", "v2"));
        assertTrue(policy.selectForConsolidation(few).isEmpty());

        var many = List.of(
                MemoryRecord.of("k1", "v1"), MemoryRecord.of("k2", "v2"), MemoryRecord.of("k3", "v3"));
        assertEquals(3, policy.selectForConsolidation(many).size());
    }

    @Test void memoryEventSealedTypes() {
        MemoryEvent event = new MemoryEvent.MemoryStored("a1", "s1", MemoryRecord.of("k", "v"), "test",
                java.time.Instant.now());
        // pattern match
        String result = switch (event) {
            case MemoryEvent.MemoryStored s -> "stored";
            case MemoryEvent.MemoryUpdated u -> "updated";
            case MemoryEvent.MemoryExpired ex -> "expired";
            case MemoryEvent.MemoryEvicted ev -> "evicted";
            case MemoryEvent.MemoryAccessed ac -> "accessed";
            case MemoryEvent.ConversationTurnObserved t -> "turn";
        };
        assertEquals("stored", result);
    }
}
