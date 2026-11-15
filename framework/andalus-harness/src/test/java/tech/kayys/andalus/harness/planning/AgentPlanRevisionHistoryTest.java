package tech.kayys.andalus.harness.planning;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgentPlanRevisionHistoryTest {

    private AgentPlan plan() {
        return new AgentPlan(
                PlanId.of("plan-history-test"),
                "execution-history-test",
                Optional.empty(),
                0L,
                PlanStatus.PROPOSED,
                List.of(PlanStep.of("Initial step")),
                Instant.parse("2026-01-01T00:00:00Z"),
                Map.of());
    }

    @Test
    void reviseAppendsImmutableHistoryWithStepLineage() {
        AgentPlan original = plan();
        PlanStep firstRevisionStep = PlanStep.of("First revision");
        AgentPlan revisionOne = original.revise(List.of(firstRevisionStep), "scope changed");
        PlanStep secondRevisionStep = PlanStep.of("Second revision");
        AgentPlan revisionTwo = revisionOne.revise(List.of(secondRevisionStep), "new evidence");

        @SuppressWarnings("unchecked")
        List<PlanRevisionHistoryEntry> history =
                (List<PlanRevisionHistoryEntry>) revisionTwo.metadata().get("revision_history");

        assertThat(history).hasSize(2);
        assertThat(history.get(0).fromRevision()).isZero();
        assertThat(history.get(0).toRevision()).isEqualTo(1L);
        assertThat(history.get(0).reason()).isEqualTo("scope changed");
        assertThat(history.get(0).previousStepIds()).containsExactly(original.steps().get(0).id());
        assertThat(history.get(0).revisedStepIds()).containsExactly(firstRevisionStep.id());
        assertThat(history.get(1).fromRevision()).isEqualTo(1L);
        assertThat(history.get(1).toRevision()).isEqualTo(2L);
        assertThat(history.get(1).reason()).isEqualTo("new evidence");
        assertThat(history.get(1).previousStepIds()).containsExactly(firstRevisionStep.id());
        assertThat(history.get(1).revisedStepIds()).containsExactly(secondRevisionStep.id());
        assertThat(original.metadata()).doesNotContainKey("revision_history");
        assertThatThrownBy(() -> history.add(history.get(0))).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void reviseRejectsMalformedExistingHistoryInsteadOfDiscardingIt() {
        AgentPlan malformed = new AgentPlan(
                PlanId.of("plan-malformed-history"),
                "execution-history-test",
                Optional.empty(),
                1L,
                PlanStatus.PROPOSED,
                List.of(PlanStep.of("Existing")),
                Instant.parse("2026-01-01T00:00:00Z"),
                Map.of("revision_history", List.of("not-a-history-entry")));

        assertThatThrownBy(() -> malformed.revise(List.of(PlanStep.of("Next")), "retry"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("unsupported entry");
    }
}
