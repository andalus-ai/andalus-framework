package tech.kayys.andalus.harness.planning;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PlanExecutionFeedbackTest {

    @Test
    void supportsEveryTerminalStepOutcome() {
        assertThat(PlanExecutionFeedback.Outcome.values()).containsExactly(
                PlanExecutionFeedback.Outcome.SUCCEEDED,
                PlanExecutionFeedback.Outcome.FAILED,
                PlanExecutionFeedback.Outcome.BLOCKED,
                PlanExecutionFeedback.Outcome.SKIPPED);
    }

    @Test
    void feedbackIsAnImmutableObservation() {
        PlanExecutionFeedback feedback = new PlanExecutionFeedback(
                PlanStepId.generate(),
                PlanExecutionFeedback.Outcome.BLOCKED,
                "Waiting for approval",
                Instant.parse("2026-11-17T00:00:00Z"),
                Map.of("dependency", "approval"));

        assertThat(feedback.outcome()).isEqualTo(PlanExecutionFeedback.Outcome.BLOCKED);
        assertThat(feedback.observedAt()).isEqualTo(Instant.parse("2026-11-17T00:00:00Z"));
        assertThat(feedback.attributes()).containsEntry("dependency", "approval");
        assertThatThrownBy(() -> feedback.attributes().put("other", "value"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void rejectsMissingOutcome() {
        assertThatThrownBy(() -> new PlanExecutionFeedback(
                PlanStepId.generate(), null, "missing state", Instant.now(), Map.of()))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("outcome");
    }
}
