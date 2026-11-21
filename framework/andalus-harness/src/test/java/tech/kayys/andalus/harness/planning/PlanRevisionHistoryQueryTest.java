package tech.kayys.andalus.harness.planning;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

        assertEquals(List.of(preserved), history.search(PlanRevisionQuery.forPlan(planId)));
        assertEquals(List.of(replaced), history.search(new PlanRevisionQuery(
                planId, 2L, 2L, PlanRevisionPolicy.ALLOW_COMPLETED_STEP_REPLACEMENT, true)));
        assertEquals(List.of(preserved), history.search(new PlanRevisionQuery(
                planId, 1L, 1L, PlanRevisionPolicy.PRESERVE_COMPLETED_STEPS, false)));
        assertEquals(List.of(), history.search(new PlanRevisionQuery(
                planId, null, null, null, true)));
    }

    @Test
    void rejectsInvalidRevisionRanges() {
        assertThrows(IllegalArgumentException.class, () -> new PlanRevisionQuery(
                PlanId.of("plan"), 3L, 2L, null, null));
        assertThrows(IllegalArgumentException.class, () -> new PlanRevisionQuery(
                PlanId.of("plan"), -1L, null, null, null));
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
