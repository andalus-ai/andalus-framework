package tech.kayys.andalus.harness.planning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;

class PlanReplanningAnalyzerTest {

    private final PlanReplanningAnalyzer analyzer = new PlanReplanningAnalyzer();

    @Test
    void preservesSuccessfulStepsAndFindsTransitiveDependentsOfFailures() {
        PlanStep research = step("research");
        PlanStep draft = step("draft", "research");
        PlanStep review = step("review", "draft");
        PlanStep unrelated = step("unrelated");

        PlanReplanningAssessment result = analyzer.analyze(
                plan(research, draft, review, unrelated),
                List.of(feedback("research", PlanExecutionFeedback.Outcome.SUCCEEDED),
                        feedback("draft", PlanExecutionFeedback.Outcome.FAILED)));

        assertThat(ids(result.completedStepIds())).containsExactly("research");
        assertThat(ids(result.directRetryStepIds())).containsExactly("draft");
        assertThat(ids(result.dependencyAffectedStepIds())).containsExactly("review");
        assertThat(ids(result.stepsRequiringReplan())).containsExactly("draft", "review");
        assertThat(ids(result.unobservedStepIds())).containsExactly("review", "unrelated");
    }

    @Test
    void blockedStepsAreDirectCandidatesAndSkippedDependentsArePreserved() {
        PlanStep source = step("source");
        PlanStep skipped = step("skipped", "source");
        PlanStep downstream = step("downstream", "skipped");

        PlanReplanningAssessment result = analyzer.analyze(
                plan(source, skipped, downstream),
                List.of(feedback("source", PlanExecutionFeedback.Outcome.BLOCKED),
                        feedback("skipped", PlanExecutionFeedback.Outcome.SKIPPED)));

        assertThat(ids(result.directRetryStepIds())).containsExactly("source");
        assertThat(ids(result.skippedStepIds())).containsExactly("skipped");
        assertThat(result.dependencyAffectedStepIds()).isEmpty();
        assertThat(ids(result.stepsRequiringReplan())).containsExactly("source");
    }

    @Test
    void explicitSuccessfulObservationStopsDependencyPropagation() {
        PlanStep first = step("first");
        PlanStep second = step("second", "first");
        PlanStep third = step("third", "second");

        PlanReplanningAssessment result = analyzer.analyze(
                plan(first, second, third),
                List.of(feedback("first", PlanExecutionFeedback.Outcome.FAILED),
                        feedback("second", PlanExecutionFeedback.Outcome.SUCCEEDED)));

        assertThat(ids(result.directRetryStepIds())).containsExactly("first");
        assertThat(result.dependencyAffectedStepIds()).isEmpty();
        assertThat(ids(result.completedStepIds())).containsExactly("second");
    }

    @Test
    void retainsDeclarationOrderAndDoesNotSelectUnrelatedUnobservedSteps() {
        PlanStep downstream = step("downstream", "failed");
        PlanStep unrelated = step("unrelated");
        PlanStep failed = step("failed");

        PlanReplanningAssessment result = analyzer.analyze(
                plan(downstream, unrelated, failed),
                List.of(feedback("failed", PlanExecutionFeedback.Outcome.FAILED)));

        assertThat(ids(result.stepsRequiringReplan())).containsExactly("downstream", "failed");
        assertThat(ids(result.unobservedStepIds())).containsExactly("downstream", "unrelated");
    }

    @Test
    void rejectsDuplicateFeedbackForOneStep() {
        assertThatThrownBy(() -> analyzer.analyze(
                plan(step("task")),
                List.of(feedback("task", PlanExecutionFeedback.Outcome.FAILED),
                        feedback("task", PlanExecutionFeedback.Outcome.SUCCEEDED))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate feedback");
    }

    @Test
    void rejectsFeedbackForUnknownStep() {
        assertThatThrownBy(() -> analyzer.analyze(
                plan(step("task")),
                List.of(feedback("other", PlanExecutionFeedback.Outcome.FAILED))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unknown step 'other'");
    }

    @Test
    void returnsImmutableAssessmentLists() {
        PlanReplanningAssessment result = analyzer.analyze(
                plan(step("task")),
                List.of(feedback("task", PlanExecutionFeedback.Outcome.SUCCEEDED)));

        assertThatThrownBy(() -> result.completedStepIds().add(PlanStepId.of("other")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private static PlanExecutionFeedback feedback(String id, PlanExecutionFeedback.Outcome outcome) {
        return new PlanExecutionFeedback(PlanStepId.of(id), outcome, "", Instant.EPOCH, Map.of());
    }

    private static AgentPlan plan(PlanStep... steps) {
        return new AgentPlan(
                PlanId.of("plan-replanning-test"),
                "execution-replanning-test",
                Optional.empty(),
                0,
                PlanStatus.PROPOSED,
                List.of(steps),
                Instant.EPOCH,
                Map.of());
    }

    private static PlanStep step(String id, String... dependencies) {
        return new PlanStep(
                PlanStepId.of(id),
                "Execute " + id,
                Optional.empty(),
                java.util.Arrays.stream(dependencies).map(PlanStepId::of).toList(),
                "",
                List.of(),
                Map.of());
    }

    private static List<String> ids(List<PlanStepId> stepIds) {
        return stepIds.stream().map(PlanStepId::value).toList();
    }
}
