package tech.kayys.andalus.harness.learning;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import static org.assertj.core.api.Assertions.assertThat;

class FeedbackEvidenceAdmissionTest {
    @Test
    void atomicallyReservesEvidenceBeforeCreatingAdmittedFeedback() {
        var trust = new FeedbackTrustPolicy.Policy(Map.of("reviewer-a", 0.9), 0.8, 1, 1);
        var store = new InMemoryFeedbackEvidenceStore();
        var signal = signal("signal-a", "reviewer-a", "trace:1");

        var accepted = FeedbackAggregation.admitAndReserve(signal, trust, Set.of(), store);
        var replay = FeedbackAggregation.admitAndReserve(
                signal("signal-b", "reviewer-a", "trace:1"), trust, Set.of(), store);

        assertThat(accepted.accepted()).isTrue();
        assertThat(replay.accepted()).isFalse();
        assertThat(replay.reasons()).anyMatch(reason -> reason.contains("EVIDENCE_ALREADY_CONSUMED"));
    }

    @Test
    void doesNotReserveEvidenceWhenTrustAdmissionFails() {
        var trust = new FeedbackTrustPolicy.Policy(Map.of("reviewer-a", 0.2), 0.8, 1, 1);
        var store = new InMemoryFeedbackEvidenceStore();

        var result = FeedbackAggregation.admitAndReserve(
                signal("signal-a", "reviewer-a", "trace:1"), trust, Set.of(), store);

        assertThat(result.accepted()).isFalse();
        assertThat(store.findOwner("trace:1")).isEmpty();
    }

    private LearningSignal signal(String id, String source, String evidence) {
        return new LearningSignal(id, LearningSignalType.HUMAN_FEEDBACK, source, "execution",
                Optional.empty(), Optional.empty(), Optional.empty(), LearningSignalPolarity.POSITIVE,
                List.of(evidence), Optional.empty(), null, Map.of());
    }
}
