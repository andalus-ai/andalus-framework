package tech.kayys.andalus.harness.planning;

import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlannerRegistryTest {

    private Planner planner(String id) {
        return new Planner() {
            @Override public String id() { return id; }
            @Override public CompletionStage<PlanningResult> plan(PlanningRequest request) {
                return CompletableFuture.completedFuture(PlanningResult.failed("unused"));
            }
        };
    }

    @Test
    void allReturnsImmutableSnapshotOrderedByPlannerId() {
        PlannerRegistry registry = new PlannerRegistry();
        Planner zulu = planner("zulu");
        Planner alpha = planner("alpha");
        registry.register(zulu);
        registry.register(alpha);

        Collection<Planner> snapshot = registry.all();

        assertThat(snapshot).extracting(Planner::id).containsExactly("alpha", "zulu");
        registry.unregister("alpha");
        registry.register(planner("bravo"));

        assertThat(snapshot).extracting(Planner::id).containsExactly("alpha", "zulu");
        assertThatThrownBy(() -> snapshot.clear()).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void registeringSameIdReplacesExistingPlanner() {
        PlannerRegistry registry = new PlannerRegistry();
        Planner original = planner("planner");
        Planner replacement = planner("planner");

        registry.register(original);
        registry.register(replacement);

        assertThat(registry.find("planner")).containsSame(replacement);
        assertThat(registry.all()).containsExactly(replacement);
    }

    @Test
    void rejectsNullPlannerAndBlankPlannerId() {
        PlannerRegistry registry = new PlannerRegistry();

        assertThatThrownBy(() -> registry.register(null))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> registry.register(planner("  ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id");
    }

    @Test
    void rejectsNullOrBlankLookupIds() {
        PlannerRegistry registry = new PlannerRegistry();

        assertThatThrownBy(() -> registry.find(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> registry.unregister(" ")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> registry.contains(null)).isInstanceOf(IllegalArgumentException.class);
    }
}
