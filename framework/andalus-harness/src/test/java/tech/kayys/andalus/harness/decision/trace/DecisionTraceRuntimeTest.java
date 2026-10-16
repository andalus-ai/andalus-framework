package tech.kayys.andalus.harness.decision.trace;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;

class DecisionTraceRuntimeTest {

    private DecisionTraceRuntime runtime;

    @BeforeEach
    void setUp() {
        runtime = new DefaultDecisionTraceRuntime();
    }

    @Test
    void testStartTrace() {
        DecisionTraceRequest request = new DecisionTraceRequest(
                "trace-100",
                "exec-1",
                "turn-1",
                "strategy-cot",
                Optional.of("goal-1"),
                Optional.of("task-1"),
                Optional.of("ctx-snapshot-1"),
                Optional.of("rev-1"),
                Map.of("tenantId", "tenant-a")
        );

        DecisionTrace trace = runtime.start(request).toCompletableFuture().join();

        assertNotNull(trace.id());
        assertTrue(trace.id().startsWith("dtrace-"));
        assertEquals("exec-1", trace.executionId());
        assertEquals("turn-1", trace.turnId());
        assertEquals("trace-100", trace.traceId());
        assertEquals(0L, trace.sequence());
        assertEquals(DecisionTraceStatus.IN_PROGRESS, trace.status());
        assertEquals(Optional.of("goal-1"), trace.goalId());
        assertEquals(Optional.of("task-1"), trace.taskId());
        assertEquals(Optional.of("ctx-snapshot-1"), trace.contextSnapshotReference());
        assertEquals(Optional.of("rev-1"), trace.stateRevision());
        assertTrue(trace.entries().isEmpty());
        assertNotNull(trace.startedAt());
        assertTrue(trace.completedAt().isEmpty());
    }

    @Test
    void testAppendEntries() {
        DecisionTrace trace = runtime.start(DecisionTraceRequest.of("exec-2", "turn-1", "strat-direct"))
                .toCompletableFuture().join();

        DecisionTraceEntry entry1 = DecisionTraceEntry.of(
                "entry-1",
                DecisionTraceEntryType.CONTEXT_RESOLVED,
                Map.of("contextKey", "value1")
        );

        DecisionTrace afterEntry1 = runtime.append(new DecisionTraceAppendRequest(trace.id(), entry1))
                .toCompletableFuture().join();

        assertEquals(1, afterEntry1.entries().size());
        assertEquals(1L, afterEntry1.sequence());
        assertEquals("entry-1", afterEntry1.entries().get(0).id());
        assertEquals(DecisionTraceEntryType.CONTEXT_RESOLVED, afterEntry1.entries().get(0).type());

        DecisionTraceEntry entry2 = DecisionTraceEntry.of(
                "entry-2",
                DecisionTraceEntryType.ACTION_DISPATCHED,
                "tool-event-42",
                List.of("evidence-1", "evidence-2"),
                Map.of("toolName", "compiler")
        );

        DecisionTrace afterEntry2 = runtime.append(new DecisionTraceAppendRequest(trace.id(), entry2))
                .toCompletableFuture().join();

        assertEquals(2, afterEntry2.entries().size());
        assertEquals(2L, afterEntry2.sequence());
        assertEquals("entry-2", afterEntry2.entries().get(1).id());
        assertEquals(Optional.of("tool-event-42"), afterEntry2.entries().get(1).sourceEventReference());
        assertEquals(List.of("evidence-1", "evidence-2"), afterEntry2.entries().get(1).evidenceReferences());
    }

    @Test
    void testDeduplicateEntries() {
        DecisionTrace trace = runtime.start(DecisionTraceRequest.of("exec-3", "turn-1", "strat-react"))
                .toCompletableFuture().join();

        DecisionTraceEntry entry = DecisionTraceEntry.of("dup-entry", DecisionTraceEntryType.DECISION_PRODUCED, Map.of());

        DecisionTrace first = runtime.append(new DecisionTraceAppendRequest(trace.id(), entry))
                .toCompletableFuture().join();
        assertEquals(1, first.entries().size());

        // Append same entry again
        DecisionTrace second = runtime.append(new DecisionTraceAppendRequest(trace.id(), entry))
                .toCompletableFuture().join();
        assertEquals(1, second.entries().size());
    }

    @Test
    void testCompleteTrace() {
        DecisionTrace trace = runtime.start(DecisionTraceRequest.of("exec-4", "turn-1", "strat-1"))
                .toCompletableFuture().join();

        DecisionTraceEntry entry = DecisionTraceEntry.of("e-1", DecisionTraceEntryType.OUTCOME_ASSESSED, Map.of());
        runtime.append(new DecisionTraceAppendRequest(trace.id(), entry)).toCompletableFuture().join();

        DecisionTraceCompletionRequest completion = DecisionTraceCompletionRequest.completed(trace.id(), "outcome-obs-99");
        DecisionTrace completed = runtime.complete(completion).toCompletableFuture().join();

        assertEquals(DecisionTraceStatus.COMPLETED, completed.status());
        assertTrue(completed.completedAt().isPresent());
        assertEquals("outcome-obs-99", completed.metadata().get("outcomeReference"));
    }

    @Test
    void testPartialTraceCompletion() {
        DecisionTrace trace = runtime.start(DecisionTraceRequest.of("exec-5", "turn-1", "strat-recovery"))
                .toCompletableFuture().join();

        DecisionTraceCompletionRequest completion = DecisionTraceCompletionRequest.of(trace.id(), DecisionTraceStatus.PARTIAL);
        DecisionTrace completed = runtime.complete(completion).toCompletableFuture().join();

        assertEquals(DecisionTraceStatus.PARTIAL, completed.status());
        assertTrue(completed.completedAt().isPresent());
    }

    @Test
    void testCannotAppendAfterCompletion() {
        DecisionTrace trace = runtime.start(DecisionTraceRequest.of("exec-6", "turn-1", "strat-terminal"))
                .toCompletableFuture().join();

        runtime.complete(DecisionTraceCompletionRequest.of(trace.id(), DecisionTraceStatus.COMPLETED))
                .toCompletableFuture().join();

        DecisionTraceEntry lateEntry = DecisionTraceEntry.of("late-entry", DecisionTraceEntryType.ACTION_COMPLETED, Map.of());
        assertThrows(CompletionException.class, () ->
                runtime.append(new DecisionTraceAppendRequest(trace.id(), lateEntry)).toCompletableFuture().join()
        );
    }

    @Test
    void testQueryTraces() {
        runtime.start(new DecisionTraceRequest("", "exec-7", "turn-1", "strat-1",
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Map.of())).toCompletableFuture().join();
        runtime.start(new DecisionTraceRequest("", "exec-7", "turn-2", "strat-1",
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Map.of())).toCompletableFuture().join();
        runtime.start(new DecisionTraceRequest("", "exec-8", "turn-1", "strat-2",
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Map.of())).toCompletableFuture().join();

        List<DecisionTrace> exec7Traces = runtime.queryTraces(DecisionTraceQuery.byExecutionId("exec-7"))
                .toCompletableFuture().join();
        assertEquals(2, exec7Traces.size());

        List<DecisionTrace> exec7Turn2Traces = runtime.queryTraces(DecisionTraceQuery.byExecutionAndTurn("exec-7", "turn-2"))
                .toCompletableFuture().join();
        assertEquals(1, exec7Turn2Traces.size());
        assertEquals("turn-2", exec7Turn2Traces.get(0).turnId());

        Optional<DecisionTrace> found = runtime.get(DecisionTraceQuery.byExecutionAndTurn("exec-7", "turn-1"))
                .toCompletableFuture().join();
        assertTrue(found.isPresent());
        assertEquals("turn-1", found.get().turnId());
    }

    @Test
    void testTraceNotFound() {
        assertThrows(CompletionException.class, () ->
                runtime.append(new DecisionTraceAppendRequest("unknown-trace", DecisionTraceEntry.of("e", DecisionTraceEntryType.ACTION_DISPATCHED, Map.of())))
                        .toCompletableFuture().join()
        );

        assertThrows(CompletionException.class, () ->
                runtime.complete(DecisionTraceCompletionRequest.of("unknown-trace", DecisionTraceStatus.FAILED))
                        .toCompletableFuture().join()
        );
    }
}
