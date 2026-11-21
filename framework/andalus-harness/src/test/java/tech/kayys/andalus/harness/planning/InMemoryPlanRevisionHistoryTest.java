package tech.kayys.andalus.harness.planning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class InMemoryPlanRevisionHistoryTest {

    @Test
    void rejectsDuplicateAndStaleRevisionAppends() {
        PlanId planId = PlanId.of("atomic-plan");
        AgentPlan initial = plan(planId, 0, "step-0");
        AgentPlan revisionOne = plan(planId, 1, "step-1");
        PlanRevisionRecord first = record(initial, revisionOne);

        InMemoryPlanRevisionHistory history = new InMemoryPlanRevisionHistory();
        history.compareAndAppend(first, 0L);

        PlanRevisionConflictException duplicate = assertThrows(
                PlanRevisionConflictException.class,
                () -> history.compareAndAppend(first, 0L));
        assertEquals(planId, duplicate.planId());
        assertEquals(0L, duplicate.expectedCurrentRevision());
        assertEquals(1L, duplicate.actualCurrentRevision());

        AgentPlan staleAlternative = plan(planId, 1, "alternative-step-1");
        PlanRevisionConflictException stale = assertThrows(
                PlanRevisionConflictException.class,
                () -> history.compareAndAppend(record(initial, staleAlternative), 0L));
        assertEquals(1L, stale.actualCurrentRevision());
        assertEquals(1, history.revisions(planId).size());
    }

    @Test
    void rejectsRevisionWhosePreviousSnapshotDiffersFromStoredHead() {
        PlanId planId = PlanId.of("snapshot-plan");
        AgentPlan initial = plan(planId, 0, "step-0");
        AgentPlan accepted = plan(planId, 1, "accepted-step");
        AgentPlan divergentPrevious = plan(planId, 1, "different-step");
        AgentPlan revised = plan(planId, 2, "step-2");

        InMemoryPlanRevisionHistory history = new InMemoryPlanRevisionHistory();
        history.append(record(initial, accepted));

        PlanRevisionConflictException conflict = assertThrows(
                PlanRevisionConflictException.class,
                () -> history.compareAndAppend(record(divergentPrevious, revised), 1L));

        assertEquals(1L, conflict.expectedCurrentRevision());
        assertEquals(1L, conflict.actualCurrentRevision());
        assertEquals(1, history.revisions(planId).size());
    }

    @Test
    void concurrentAppendsForSameExpectedRevisionAllowOnlyOneWinner() throws Exception {
        PlanId planId = PlanId.of("concurrent-plan");
        AgentPlan initial = plan(planId, 0, "step-0");
        AgentPlan revised = plan(planId, 1, "step-1");
        PlanRevisionRecord record = record(initial, revised);
        InMemoryPlanRevisionHistory history = new InMemoryPlanRevisionHistory();
        CountDownLatch start = new CountDownLatch(1);
        AtomicInteger successes = new AtomicInteger();
        AtomicInteger conflicts = new AtomicInteger();

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            Future<?> first = executor.submit(() -> appendAfterStart(history, record, start, successes, conflicts));
            Future<?> second = executor.submit(() -> appendAfterStart(history, record, start, successes, conflicts));
            start.countDown();
            first.get();
            second.get();
        }

        assertEquals(1, successes.get());
        assertEquals(1, conflicts.get());
        assertEquals(1, history.revisions(planId).size());
    }

    private static void appendAfterStart(
            InMemoryPlanRevisionHistory history,
            PlanRevisionRecord record,
            CountDownLatch start,
            AtomicInteger successes,
            AtomicInteger conflicts
    ) {
        try {
            start.await();
            history.compareAndAppend(record, record.fromRevision());
            successes.incrementAndGet();
        } catch (PlanRevisionConflictException expected) {
            conflicts.incrementAndGet();
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(interrupted);
        }
    }

    private static PlanRevisionRecord record(AgentPlan previous, AgentPlan revised) {
        return PlanRevisionRecord.of(previous, revised, "test revision", List.of());
    }

    private static AgentPlan plan(PlanId id, long revision, String stepId) {
        PlanStep step = new PlanStep(PlanStepId.of(stepId), stepId, Optional.empty(),
                List.of(), "", List.of(), Map.of());
        return new AgentPlan(id, "execution", Optional.empty(), revision,
                PlanStatus.PROPOSED, List.of(step), Instant.EPOCH, Map.of());
    }
}
