package tech.kayys.andalus.harness.reasoning;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReasonerRegistryTest {

    private ContextAwareReasoner reasoner(String id) {
        return new ContextAwareReasoner() {
            @Override
            public String id() {
                return id;
            }

            @Override
            public java.util.concurrent.CompletionStage<ReasoningAssessment> reason(ReasoningRequest request) {
                return CompletableFuture.completedFuture(
                        ReasoningAssessment.of(request.purpose(), id));
            }
        };
    }

    @Test
    void findForSelectsLexicographicallyFirstSupportingReasoner() {
        ReasonerRegistry registry = new ReasonerRegistry();
        registry.register(reasoner("zeta"));
        registry.register(reasoner("alpha"));
        registry.register(reasoner("middle"));

        assertThat(registry.findFor(ReasoningPurpose.EVALUATE_PLAN))
                .get()
                .extracting(ContextAwareReasoner::id)
                .isEqualTo("alpha");
    }

    @Test
    void allReturnsOrderedImmutableSnapshot() {
        ReasonerRegistry registry = new ReasonerRegistry();
        registry.register(reasoner("zeta"));
        registry.register(reasoner("alpha"));

        var snapshot = registry.all();
        registry.register(reasoner("beta"));

        assertThat(snapshot).extracting(ContextAwareReasoner::id)
                .containsExactly("alpha", "zeta");
        assertThatThrownBy(() -> snapshot.clear())
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(registry.all()).extracting(ContextAwareReasoner::id)
                .containsExactly("alpha", "beta", "zeta");
    }

    @Test
    void registeringSameIdReplacesPreviousProvider() {
        ReasonerRegistry registry = new ReasonerRegistry();
        ContextAwareReasoner first = reasoner("same");
        ContextAwareReasoner replacement = reasoner("same");

        registry.register(first);
        registry.register(replacement);

        assertThat(registry.size()).isEqualTo(1);
        assertThat(registry.find("same")).containsSame(replacement);
    }

    @Test
    void rejectsNullReasonerAndBlankIds() {
        ReasonerRegistry registry = new ReasonerRegistry();

        assertThatThrownBy(() -> registry.register(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("reasoner");
        assertThatThrownBy(() -> registry.register(reasoner("  ")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("ID");
    }

    @Test
    void findForRejectsNullPurpose() {
        ReasonerRegistry registry = new ReasonerRegistry();

        assertThatThrownBy(() -> registry.findFor(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("purpose");
    }
}
