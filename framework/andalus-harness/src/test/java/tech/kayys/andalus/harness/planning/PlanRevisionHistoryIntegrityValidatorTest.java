package tech.kayys.andalus.harness.planning;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class PlanRevisionHistoryIntegrityValidatorTest {

    @Test
    void acceptsContiguousOrderedHistoryWithMatchingSnapshots() {
        PlanId id = PlanId.of("integrity-plan");
        AgentPlan zero = plan(id, 0, "zero");
        AgentPlan one = plan(id, 1, "one");
        AgentPlan two = plan(id, 2, "two");

        PlanRevisionHistoryIntegrityReport report =
                PlanRevisionHistoryIntegrityValidator.validate(id, List.of(
                        PlanRevisionRecord.of(zero, one, "first", List.of()),
                        PlanRevisionRecord.of(one, two, "second", List.of())));

        assertTrue(report.valid(), report.errors().toString());
    }

    @Test
    void reportsRevisionGapsAndSnapshotDivergence() {
        PlanId id = PlanId.of("integrity-plan");
        AgentPlan zero = plan(id, 0, "zero");
        AgentPlan one = plan(id, 1, "one");
        AgentPlan divergentOne = plan(id, 1, "different-one");
        AgentPlan two = plan(id, 2, "two");

        PlanRevisionHistoryIntegrityReport report =
                PlanRevisionHistoryIntegrityValidator.validate(id, List.of(
                        PlanRevisionRecord.of(zero, one, "first", List.of()),
                        PlanRevisionRecord.of(divergentOne, two, "second", List.of())));

        assertFalse(report.valid());
        org.junit.jupiter.api.Assertions.assertTrue(report.errors().stream()
                .anyMatch(error -> error.contains("discontinuous")));
        org.junit.jupiter.api.Assertions.assertTrue(report.errors().stream()
                .anyMatch(error -> error.contains("snapshot continuity")));
    }

    @Test
    void reportsWrongPlanAndOutOfOrderRecords() {
        PlanId expected = PlanId.of("expected");
        PlanId other = PlanId.of("other");
        AgentPlan expectedZero = plan(expected, 0, "zero");
        AgentPlan expectedOne = plan(expected, 1, "one");
        AgentPlan otherOne = plan(other, 1, "other-one");
        AgentPlan otherTwo = plan(other, 2, "other-two");

        PlanRevisionHistoryIntegrityReport report =
                PlanRevisionHistoryIntegrityValidator.validate(expected, List.of(
                        PlanRevisionRecord.of(expectedZero, expectedOne, "first", List.of()),
                        PlanRevisionRecord.of(otherOne, otherTwo, "foreign", List.of())));

        assertFalse(report.valid());
        org.junit.jupiter.api.Assertions.assertTrue(report.errors().stream()
                .anyMatch(error -> error.contains("another plan")));
    }

    private static AgentPlan plan(PlanId id, long revision, String description) {
        PlanStep step = new PlanStep(PlanStepId.of("step-" + description), description,
                Optional.empty(), List.of(), "", List.of(), Map.of());
        return new AgentPlan(id, "execution", Optional.empty(), revision,
                PlanStatus.PROPOSED, List.of(step), Instant.EPOCH, Map.of());
    }
}
