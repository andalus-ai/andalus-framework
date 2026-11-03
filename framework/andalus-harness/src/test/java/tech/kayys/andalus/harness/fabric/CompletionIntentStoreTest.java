package tech.kayys.andalus.harness.fabric;

import org.junit.jupiter.api.Test;
import tech.kayys.andalus.execution.attempt.AttemptId;
import tech.kayys.andalus.execution.mobility.FencingToken;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CompletionIntentStoreTest {
    private CompletionIntent intent(String key) {
        AssignmentId assignmentId = AssignmentId.of("assignment-1");
        TaskClaimToken claim = new TaskClaimToken(assignmentId, WorkerId.of("worker-1"), 4);
        return new CompletionIntent(key, "execution-1", AttemptId.of("physical-attempt-1"),
                assignmentId, 8, claim, FencingToken.of(12),
                CompletionIntentStage.RECORDED, Instant.parse("2026-11-03T00:00:00Z"));
    }

    @Test
    void createIsIdempotentForSameOperation() {
        InMemoryCompletionIntentStore store = new InMemoryCompletionIntentStore();
        CompletionIntent original = intent("complete-1");

        assertEquals(original, store.createIfAbsent(original));
        assertEquals(original, store.createIfAbsent(intent("complete-1")));
    }

    @Test
    void idempotencyKeyCannotBeReusedForDifferentOperation() {
        InMemoryCompletionIntentStore store = new InMemoryCompletionIntentStore();
        store.createIfAbsent(intent("complete-1"));
        CompletionIntent different = new CompletionIntent("complete-1", "execution-2",
                AttemptId.of("physical-attempt-2"), AssignmentId.of("assignment-2"), 8,
                new TaskClaimToken(AssignmentId.of("assignment-2"), WorkerId.of("worker-1"), 4),
                FencingToken.of(12), CompletionIntentStage.RECORDED,
                Instant.parse("2026-11-03T00:00:00Z"));

        assertThrows(IllegalStateException.class, () -> store.createIfAbsent(different));
    }

    @Test
    void stageAdvancementIsCompareAndSetAndMonotonic() {
        InMemoryCompletionIntentStore store = new InMemoryCompletionIntentStore();
        store.createIfAbsent(intent("complete-1"));

        assertFalse(store.advance("complete-1", CompletionIntentStage.EXECUTION_STATE_COMMITTED,
                CompletionIntentStage.ATTEMPT_COMMITTED));
        assertTrue(store.advance("complete-1", CompletionIntentStage.RECORDED,
                CompletionIntentStage.EXECUTION_STATE_COMMITTED));
        assertFalse(store.advance("complete-1", CompletionIntentStage.RECORDED,
                CompletionIntentStage.EXECUTION_STATE_COMMITTED));
        assertEquals(CompletionIntentStage.EXECUTION_STATE_COMMITTED,
                store.get("complete-1").orElseThrow().stage());
    }

    @Test
    void rejectsSkippedStageTransitions() {
        InMemoryCompletionIntentStore store = new InMemoryCompletionIntentStore();
        store.createIfAbsent(intent("complete-1"));

        assertThrows(IllegalArgumentException.class, () ->
                store.advance("complete-1", CompletionIntentStage.RECORDED,
                        CompletionIntentStage.ATTEMPT_COMMITTED));
    }
}
