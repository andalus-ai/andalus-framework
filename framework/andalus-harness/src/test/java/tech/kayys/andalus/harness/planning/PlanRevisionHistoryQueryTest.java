package tech.kayys.andalus.harness.planning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class PlanRevisionHistoryQueryTest {

    @Test
    void filtersByInclusiveRevisionRangePolicyAndCompletedWorkReplacement() {
        PlanId planId = PlanId.of("plan-query");
        PlanStep completed = step("completed", "Completed");
        AgentPlan revision0 = plan(planId, 0, List.of(completed));
        AgentPlan revision1 = plan(planId, 1, List.of(completed));
        AgentPlan revision2 = plan(planId, 2, List.of(step("replacement", "Replacement")));

        PlanRevisionRecord preserved = PlanRevisionRecord.of(
                revision0, revision1, "no structural change", List.of(),
                PlanRevisionPolicy.PRESERVE_COMPLETED_STEPS);
        PlanRevisionRecord replaced = PlanRevisionRecord.of(
                revision1, revision2, "requirements changed",
                List.of(feedback("completed", PlanExecutionFeedback.Outcome.SUCCEEDED)),
                PlanRevisionPolicy.ALLOW_COMPLETED_STEP_REPLACEMENT);

        InMemoryPlanRevisionHistory history = new InMemoryPlanRevisionHistory();
        history.append(preserved);
        history.append(replaced);

        assertEquals(List.of(preserved, replaced), history.search(PlanRevisionQuery.forPlan(planId)));
        assertEquals(List.of(replaced), history.search(new PlanRevisionQuery(
                planId, 2L, 2L, PlanRevisionPolicy.ALLOW_COMPLETED_STEP_REPLACEMENT, true)));
        assertEquals(List.of(preserved), history.search(new PlanRevisionQuery(
                planId, 1L, 1L, PlanRevisionPolicy.PRESERVE_COMPLETED_STEPS, false)));
        assertEquals(List.of(replaced), history.search(new PlanRevisionQuery(
                planId, null, null, null, true)));
    }

    @Test
    void rejectsInvalidRevisionRanges() {
        assertThrows(IllegalArgumentException.class, () -> new PlanRevisionQuery(
                PlanId.of("plan"), 3L, 2L, null, null));
        assertThrows(IllegalArgumentException.class, () -> new PlanRevisionQuery(
                PlanId.of("plan"), -1L, null, null, null));
    }

    @Test
    void pagesHistoryWithExclusiveStableRevisionCursor() {
        PlanId planId = PlanId.of("plan-pages");
        AgentPlan revision0 = plan(planId, 0, List.of(step("s0", "Step 0")));
        AgentPlan revision1 = plan(planId, 1, List.of(step("s1", "Step 1")));
        AgentPlan revision2 = plan(planId, 2, List.of(step("s2", "Step 2")));
        AgentPlan revision3 = plan(planId, 3, List.of(step("s3", "Step 3")));
        PlanRevisionRecord first = PlanRevisionRecord.of(revision0, revision1, "first", List.of());
        PlanRevisionRecord second = PlanRevisionRecord.of(revision1, revision2, "second", List.of());
        PlanRevisionRecord third = PlanRevisionRecord.of(revision2, revision3, "third", List.of());

        InMemoryPlanRevisionHistory history = new InMemoryPlanRevisionHistory();
        history.append(first);
        history.append(second);
        history.append(third);

        PlanRevisionPage pageOne = history.page(PlanRevisionPageRequest.firstPage(
                PlanRevisionQuery.forPlan(planId), 2));
        assertEquals(List.of(first, second), pageOne.records());
        assertEquals(2L, pageOne.nextCursor());
        assertTrue(pageOne.hasMore());

        PlanRevisionPage pageTwo = history.page(new PlanRevisionPageRequest(
                PlanRevisionQuery.forPlan(planId), 2, pageOne.nextCursor()));
        assertEquals(List.of(third), pageTwo.records());
        assertNull(pageTwo.nextCursor());
        assertFalse(pageTwo.hasMore());
    }

    @Test
    void validatesPageSizeAndCursor() {
        PlanRevisionQuery query = PlanRevisionQuery.forPlan(PlanId.of("plan"));
        assertThrows(IllegalArgumentException.class,
                () -> PlanRevisionPageRequest.firstPage(query, 0));
        assertThrows(IllegalArgumentException.class,
                () -> PlanRevisionPageRequest.firstPage(query, PlanRevisionPageRequest.MAX_PAGE_SIZE + 1));
        assertThrows(IllegalArgumentException.class,
                () -> new PlanRevisionPageRequest(query, 10, -1L));
    }

    private static PlanStep step(String id, String description) {
        return new PlanStep(PlanStepId.of(id), description, Optional.empty(),
                List.of(), "", List.of(), Map.of());
    }

    private static AgentPlan plan(PlanId id, long revision, List<PlanStep> steps) {
        return new AgentPlan(id, "execution", Optional.empty(), revision,
                PlanStatus.PROPOSED, steps, Instant.EPOCH, Map.of());
    }

    private static PlanExecutionFeedback feedback(String id, PlanExecutionFeedback.Outcome outcome) {
        return new PlanExecutionFeedback(PlanStepId.of(id), outcome, "", Instant.EPOCH, Map.of());
    }
}
