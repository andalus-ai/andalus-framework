package tech.kayys.andalus.harness.fabric;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.harness.consistency.state.ExecutionId;
import tech.kayys.andalus.workflow.graph.NodeId;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskQueueTest {
    private static final class MutableClock extends Clock {
        private final AtomicReference<Instant> now = new AtomicReference<>(Instant.parse("2026-01-01T00:00:00Z"));
        @Override public ZoneId getZone() { return ZoneId.of("UTC"); }
        @Override public Clock withZone(ZoneId zone) { return this; }
        @Override public Instant instant() { return now.get(); }
        void advance(Duration duration) { now.updateAndGet(value -> value.plus(duration)); }
    }

    private DefaultExecutionAssignment assignment(String id) {
        AssignmentId assignmentId = AssignmentId.of(id);
        WorkerId owner = WorkerId.of("initial-owner");
        return new DefaultExecutionAssignment(assignmentId, ExecutionId.of("exec-" + id),
                NodeId.of("node-" + id), AttemptId.initial(), owner,
                AssignmentFence.of(assignmentId, 1), AssignmentStatus.PENDING,
                AssignmentConstraints.defaults());
    }

    @Test
    void enqueueIsIdempotentButRejectsKeyReuseForDifferentAssignment() {
        TaskQueue queue = new InMemoryTaskQueue();
        ExecutionAssignment assignment = assignment("a");
        queue.enqueue("key-a", assignment);
        queue.enqueue("key-a", assignment);
        assertEquals(1, queue.claim(WorkerId.of("worker-a"), 10, Duration.ofSeconds(30)).size());
        assertThrows(IllegalStateException.class, () -> queue.enqueue("key-a", assignment("b")));
    }

    @Test
    void onlyOneConcurrentClaimCanOwnAnAssignment() throws Exception {
        TaskQueue queue = new InMemoryTaskQueue();
        queue.enqueue("key-a", assignment("a"));
        var pool = java.util.concurrent.Executors.newFixedThreadPool(2);
        try {
            var first = pool.submit(() -> queue.claim(WorkerId.of("w1"), 1, Duration.ofSeconds(30)));
            var second = pool.submit(() -> queue.claim(WorkerId.of("w2"), 1, Duration.ofSeconds(30)));
            int total = first.get().size() + second.get().size();
            assertEquals(1, total);
        } finally {
            pool.shutdownNow();
        }
    }

    @Test
    void staleOwnerCannotAcknowledgeAfterVisibilityExpiryAndRedelivery() {
        MutableClock clock = new MutableClock();
        TaskQueue queue = new InMemoryTaskQueue(clock);
        queue.enqueue("key-a", assignment("a"));
        TaskClaim first = queue.claim(WorkerId.of("w1"), 1, Duration.ofSeconds(5)).get(0);
        clock.advance(Duration.ofSeconds(5));
        assertFalse(queue.acknowledge(first.token()));
        TaskClaim second = queue.claim(WorkerId.of("w2"), 1, Duration.ofSeconds(5)).get(0);
        assertEquals(first.token().generation() + 1, second.token().generation());
        assertFalse(queue.acknowledge(first.token()));
        assertTrue(queue.acknowledge(second.token()));
        assertFalse(queue.acknowledge(second.token()));
    }

    @Test
    void negativeAcknowledgementSchedulesDelayedRedeliveryAndInvalidatesOldToken() {
        MutableClock clock = new MutableClock();
        TaskQueue queue = new InMemoryTaskQueue(clock);
        queue.enqueue("key-a", assignment("a"));
        TaskClaim first = queue.claim(WorkerId.of("w1"), 1, Duration.ofSeconds(30)).get(0);
        assertTrue(queue.negativeAcknowledge(first.token(), Duration.ofSeconds(10)));
        assertFalse(queue.acknowledge(first.token()));
        assertTrue(queue.claim(WorkerId.of("w2"), 1, Duration.ofSeconds(30)).isEmpty());
        clock.advance(Duration.ofSeconds(10));
        TaskClaim second = queue.claim(WorkerId.of("w2"), 1, Duration.ofSeconds(30)).get(0);
        assertEquals(first.token().generation() + 1, second.token().generation());
    }

    @Test
    void onlyCurrentClaimCanExtendVisibility() {
        MutableClock clock = new MutableClock();
        TaskQueue queue = new InMemoryTaskQueue(clock);
        queue.enqueue("key-a", assignment("a"));
        TaskClaim claim = queue.claim(WorkerId.of("w1"), 1, Duration.ofSeconds(5)).get(0);
        assertTrue(queue.extendVisibility(claim.token(), Duration.ofSeconds(10)));
        clock.advance(Duration.ofSeconds(6));
        assertTrue(queue.acknowledge(claim.token()));
    }
}
