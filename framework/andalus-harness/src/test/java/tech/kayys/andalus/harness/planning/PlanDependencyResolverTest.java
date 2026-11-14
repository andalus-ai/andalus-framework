package tech.kayys.andalus.harness.planning;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.junit.jupiter.api.Test;

class PlanDependencyResolverTest {

    private final PlanDependencyResolver resolver = new PlanDependencyResolver();

    @Test
    void resolvesDependenciesBeforeDependentsAndKeepsStableOrderForReadySteps() {
        PlanStep research = step("research");
        PlanStep draft = step("draft", "research");
        PlanStep review = step("review");
        PlanStep publish = step("publish", "draft", "review");

        List<PlanStep> ordered = resolver.resolve(plan(
                research, draft, review, publish));

        assertThat(ids(ordered)).containsExactly(
                "research", "draft", "review", "publish");
    }

    @Test
    void doesNotRequireDeclarationOrderToMatchDependencyOrder() {
        PlanStep publish = step("publish", "draft");
        PlanStep draft = step("draft", "research");
        PlanStep research = step("research");

        assertThat(ids(resolver.resolve(plan(publish, draft, research))))
                .containsExactly("research", "draft", "publish");
    }

    @Test
    void treatsRepeatedDependencyReferencesAsOneEdge() {
        PlanStep source = step("source");
        PlanStep target = step("target", "source", "source");

        assertThat(ids(resolver.resolve(plan(source, target))))
                .containsExactly("source", "target");
    }

    @Test
    void rejectsUnknownDependencies() {
        assertThatThrownBy(() -> resolver.resolve(plan(step("task", "missing"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("unknown dependency 'missing'");
    }

    @Test
    void rejectsDependencyCycles() {
        PlanStep first = step("first", "second");
        PlanStep second = step("second", "first");

        assertThatThrownBy(() -> resolver.resolve(plan(first, second)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dependency cycle");
    }

    @Test
    void rejectsDuplicateStepIds() {
        assertThatThrownBy(() -> resolver.resolve(plan(step("same"), step("same"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate step ID: 'same'");
    }

    @Test
    void returnsAnImmutableOrdering() {
        List<PlanStep> ordered = resolver.resolve(plan(step("task")));

        assertThatThrownBy(() -> ordered.add(step("other")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    private static AgentPlan plan(PlanStep... steps) {
        return new AgentPlan(
                PlanId.of("plan-test"),
                "execution-test",
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

    private static List<String> ids(List<PlanStep> steps) {
        return steps.stream().map(step -> step.id().value()).toList();
    }
}
