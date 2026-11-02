package tech.kayys.andalus.harness.learning;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackTrustPolicyTest {

    private LearningSignal feedback(String source, List<String> evidence) {
        return new LearningSignal("feedback-" + source, LearningSignalType.HUMAN_FEEDBACK,
                source, "execution-1", Optional.empty(), Optional.empty(), Optional.empty(),
                LearningSignalPolarity.MIXED, evidence, Optional.empty(), null, Map.of());
    }

    @Test
    void acceptsFeedbackOnlyWhenSourceEvidenceAndCorroborationMeetPolicy() {
        FeedbackTrustPolicy.Policy policy = new FeedbackTrustPolicy.Policy(
                Map.of("reviewer-a", 0.95, "reviewer-b", 0.9), 0.8, 2, 2);

        FeedbackTrustPolicy.Decision decision = FeedbackTrustPolicy.assess(
                feedback("reviewer-a", List.of("trace:1", "trace:2")), policy,
                Set.of(), Set.of("reviewer-b"));

        assertThat(decision.accepted()).isTrue();
        assertThat(decision.trustScore()).isEqualTo(0.95);
        assertThat(decision.reasons()).isEmpty();
    }

    @Test
    void rejectsUnknownOrLowTrustSource() {
        FeedbackTrustPolicy.Policy policy = new FeedbackTrustPolicy.Policy(
                Map.of("reviewer-a", 0.4), 0.8, 1, 1);

        FeedbackTrustPolicy.Decision decision = FeedbackTrustPolicy.assess(
                feedback("unregistered-reviewer", List.of("trace:1")), policy, Set.of(), Set.of());

        assertThat(decision.accepted()).isFalse();
        assertThat(decision.reasons()).anyMatch(reason -> reason.contains("untrusted"));
    }

    @Test
    void rejectsEvidenceReplayAndInsufficientEvidence() {
        FeedbackTrustPolicy.Policy policy = new FeedbackTrustPolicy.Policy(
                Map.of("reviewer-a", 1.0, "reviewer-b", 1.0), 0.8, 2, 2);

        FeedbackTrustPolicy.Decision replay = FeedbackTrustPolicy.assess(
                feedback("reviewer-a", List.of("trace:1")), policy,
                Set.of("trace:1"), Set.of("reviewer-b"));

        assertThat(replay.accepted()).isFalse();
        assertThat(replay.reasons()).anyMatch(reason -> reason.contains("already used"));
        assertThat(replay.reasons()).anyMatch(reason -> reason.contains("insufficient"));
    }

    @Test
    void rejectsAutomatedEvaluationSignalsFromHumanFeedbackAdmission() {
        LearningSignal automated = new LearningSignal("eval-1", LearningSignalType.EVALUATION,
                "evaluation:judge", "evaluation-1", Optional.empty(), Optional.empty(), Optional.empty(),
                LearningSignalPolarity.POSITIVE, List.of("evaluation:1"), Optional.of("evaluation-1"),
                null, Map.of());
        FeedbackTrustPolicy.Policy policy = new FeedbackTrustPolicy.Policy(
                Map.of("evaluation:judge", 1.0), 0.5, 1, 1);

        FeedbackTrustPolicy.Decision decision = FeedbackTrustPolicy.assess(
                automated, policy, Set.of(), Set.of());

        assertThat(decision.accepted()).isFalse();
        assertThat(decision.reasons()).anyMatch(reason -> reason.contains("only HUMAN_FEEDBACK"));
    }

    @Test
    void validatesTrustConfiguration() {
        assertThatThrownBy(() -> new FeedbackTrustPolicy.Policy(
                Map.of("reviewer-a", 1.5), 0.5, 1, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
