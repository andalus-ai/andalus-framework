package tech.kayys.andalus.harness.planning;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class PlanRevisionDiffCalculatorTest {

    @Test
    void classifiesAddedRemovedModifiedAndPreservedStepsInDeclarationOrder() {
        PlanStep preserved = step("keep", "Keep");
        PlanStep modifiedBefore = step("modify", "Before");
        PlanStep removed = step("remove", "Remove");
        AgentPlan previous = plan(0, List.of(preserved, modifiedBefore, removed));
        AgentPlan revised = plan(1, List.of(
                step("added", "Added"),
                preserved,
                step("modify", "After")));

        PlanRevisionDiff diff = PlanRevisionDiffCalculator.calculate(previous, revised, List.of());

        assertEquals(List.of(id("added")), diff.addedStepIds());
        assertEquals(List.of(id("remove")), diff.removedStepIds());
        assertEquals(List.of(id("modify")), diff.modifiedStepIds());
        assertEquals(List.of(id("keep")), diff.preservedStepIds());
        assertEquals(List.of(), diff.replacedCompletedStepIds());
    }

    @Test
    void reportsSuccessfulStepsWhoseContinuityWasBrokenWithoutGuessingReplacementPairs() {
        AgentPlan previous = plan(2, List.of(
                step("changed", "Original"),
                step("deleted", "Completed and removed"),
                step("kept", "Still here")));
        AgentPlan revised = plan(3, List.of(
                step("new-step", "New work"),
                step("changed", "Changed implementation"),
                step("kept", "Still here")));
        List<PlanExecutionFeedback> feedback = List.of(
                feedback("changed", PlanExecutionFeedback.Outcome.SUCCEEDED),
                feedback("deleted", PlanExecutionFeedback.Outcome.SUCCEEDED));

        PlanRevisionDiff diff = PlanRevisionDiffCalculator.calculate(previous, revised, feedback);

        assertEquals(List.of(id("new-step")), diff.addedStepIds());
        assertEquals(List.of(id("deleted")), diff.removedStepIds());
        assertEquals(List.of(id("changed")), diff.modifiedStepIds());
        assertEquals(List.of(id("kept")), diff.preservedStepIds());
        assertEquals(List.of(id("changed"), id("deleted")), diff.replacedCompletedStepIds());
    }

    @Test
    void rejectsDuplicateStepIdsAndFeedbackForUnknownSteps() {
        AgentPlan duplicate = plan(0, List.of(step("same", "One"), step("same", "Two")));
        AgentPlan valid = plan(1, List.of(step("same", "One")));
        assertThrows(IllegalArgumentException.class,
                () -> PlanRevisionDiffCalculator.calculate(duplicate, valid, List.of()));

        AgentPlan previous = plan(0, List.of(step("known", "Known")));
        assertThrows(IllegalArgumentException.class,
                () -> PlanRevisionDiffCalculator.calculate(previous, valid,
                        List.of(feedback("unknown", PlanExecutionFeedback.Outcome.SUCCEEDED))));
    }

    private static PlanStep step(String id, String description) {
        return new PlanStep(id(id), description, Optional.empty(), List.of(), "", List.of(), Map.of());
    }

    private static PlanStepId id(String value) {
        return PlanStepId.of(value);
    }

    private static AgentPlan plan(long revision, List<PlanStep> steps) {
        return new AgentPlan(PlanId.generate(), "execution", Optional.empty(), revision,
                PlanStatus.PROPOSED, steps, Instant.EPOCH, Map.of());
    }

    private static PlanExecutionFeedback feedback(String id, PlanExecutionFeedback.Outcome outcome) {
        return new PlanExecutionFeedback(id(id), outcome, "", Instant.EPOCH, Map.of());
    }
}
