package tech.kayys.andalus.harness.learning;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FeedbackEvidenceAdmissionTest {

    private LearningSignal feedback(String id, String source, List<String> evidence) {
        return new LearningSignal(id, LearningSignalType.HUMAN_FEEDBACK, source, "execution-1",
                Optional.empty(), Optional.empty(), Optional.empty(), LearningSignalPolarity.POSITIVE,
                evidence, Optional.empty(), null, Map.of());
    }

    private FeedbackTrustPolicy.Policy trustPolicy() {
        return new FeedbackTrustPolicy.Policy(Map.of("reviewer-a", 0.95, "reviewer-b", 0.9),
                0.8, 1, 2);
    }

    @Test
    void reservesEvidenceOnlyAfterTrustAdmission() {
        var store = new InMemoryFeedbackEvidenceStore();
        var rejected = FeedbackAggregation.admitAndReserve(
                feedback("bad", "unknown", List.of("trace:1")), trustPolicy(), Set.of("reviewer-b"), store);

        assertThat(rejected.accepted()).isFalse();
        assertThat(store.findOwner("trace:1")).isEmpty();
    }

    @Test
    void rejectsReplayAndPreservesOriginalOwner() {
        var store = new InMemoryFeedbackEvidenceStore();
        var first = FeedbackAggregation.admitAndReserve(
                feedback("feedback-1", "reviewer-a", List.of("trace:1")),
                trustPolicy(), Set.of("reviewer-b"), store);
        var replay = FeedbackAggregation.admitAndReserve(
                feedback("feedback-2", "reviewer-a", List.of("trace:1")),
                trustPolicy(), Set.of("reviewer-b"), store);

        assertThat(first.accepted()).isTrue();
        assertThat(replay.accepted()).isFalse();
        assertThat(store.findOwner("trace:1")).contains("feedback-1");
    }

    @Test
    void exactRetryIsIdempotentlyAccepted() {
        var store = new InMemoryFeedbackEvidenceStore();
        var signal = feedback("feedback-1", "reviewer-a", List.of("trace:1"));
        var first = FeedbackAggregation.admitAndReserve(signal, trustPolicy(), Set.of("reviewer-b"), store);
        var retry = FeedbackAggregation.admitAndReserve(signal, trustPolicy(), Set.of("reviewer-b"), store);

        assertThat(first.accepted()).isTrue();
        assertThat(retry.accepted()).isTrue();
    }
}
