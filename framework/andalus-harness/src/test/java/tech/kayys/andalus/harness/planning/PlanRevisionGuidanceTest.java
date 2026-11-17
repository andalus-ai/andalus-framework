package tech.kayys.andalus.harness.planning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class PlanRevisionGuidanceTest {

    @Test
    void guidancePreservesOriginalSuccessfulStepsAndSeparatesRetryCandidates() {
        PlanStep done = step("done");
        PlanStep failed = step("failed");
        PlanStep dependent = step("dependent", "failed");
        PlanStep skipped = step("skipped");

        AgentPlan plan = plan(done, failed, dependent, skipped);
        PlanReplanningAssessment assessment = new PlanReplanningAnalyzer().analyze(plan, List.of(
                feedback("done", PlanExecutionFeedback.Outcome.SUCCEEDED),
                feedback("failed", PlanExecutionFeedback.Outcome.FAILED),
                feedback("skipped", PlanExecutionFeedback.Outcome.SKIPPED)));

        PlanRevisionGuidance guidance = PlanRevisionGuidance.from(plan, assessment);

        assertThat(guidance.preservedSteps()).containsExactly(done);
        assertThat(guidance.stepsToReplan()).containsExactly(failed, dependent);
        assertThat(guidance.dependencyAffectedSteps()).containsExactly(dependent);
        assertThat(guidance.skippedSteps()).containsExactly(skipped);
        assertThat(guidance.unobservedSteps()).containsExactly(dependent);
        assertThat(guidance.preservedSteps().get(0)).isSameAs(done);
    }

    @Test
    void rejectsAssessmentContainingUnknownStepIds() {
        AgentPlan plan = plan(step("known"));
        PlanReplanningAssessment invalid = new PlanReplanningAssessment(
                List.of(PlanStepId.of("missing")), List.of(), List.of(), List.of(), List.of(), List.of());

        assertThatThrownBy(() -> PlanRevisionGuidance.from(plan, invalid))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("outside the current plan");
    }

    @Test
    void guidanceListsAreImmutable() {
        PlanRevisionGuidance guidance = PlanRevisionGuidance.from(
                plan(step("task")),
                new PlanReplanningAssessment(List.of(), List.of(), List.of(), List.of(), List.of(), List.of()));

        assertThatThrownBy(() -> guidance.stepsToReplan().add(step("other")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private static PlanExecutionFeedback feedback(String id, PlanExecutionFeedback.Outcome outcome) {
        return new PlanExecutionFeedback(PlanStepId.of(id), outcome, "", Instant.EPOCH, Map.of());
    }

    private static AgentPlan plan(PlanStep... steps) {
        return new AgentPlan(PlanId.of("revision-guidance-test"), "execution-guidance-test",
                Optional.empty(), 0, PlanStatus.PROPOSED, List.of(steps), Instant.EPOCH, Map.of());
    }

    private static PlanStep step(String id, String... dependencies) {
        return new PlanStep(PlanStepId.of(id), "Execute " + id, Optional.empty(),
                java.util.Arrays.stream(dependencies).map(PlanStepId::of).toList(), "", List.of(), Map.of());
    }
}
