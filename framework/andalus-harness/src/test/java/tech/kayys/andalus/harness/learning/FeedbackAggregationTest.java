package tech.kayys.andalus.harness.learning;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FeedbackAggregationTest {

    private FeedbackAggregation.AdmittedFeedback admitted(String source, String evidence,
                                                           LearningSignalPolarity polarity, double trust) {
        LearningSignal signal = new LearningSignal("id-" + source, LearningSignalType.HUMAN_FEEDBACK,
                source, "execution-" + source, Optional.empty(), Optional.empty(), Optional.empty(),
                polarity, List.of(evidence), Optional.empty(), null, Map.of());
        return new FeedbackAggregation.AdmittedFeedback(signal,
                new FeedbackTrustPolicy.Decision(true, trust, List.of()));
    }

    private FeedbackAggregation.Policy policy() {
        return new FeedbackAggregation.Policy(2, 2, 0.7);
    }

    @Test
    void reachesPositiveConsensusUsingTrustWeightedEvidence() {
        var result = FeedbackAggregation.aggregate("agent:planner", List.of(
                admitted("reviewer-a", "trace:1", LearningSignalPolarity.POSITIVE, 0.9),
                admitted("reviewer-b", "trace:2", LearningSignalPolarity.POSITIVE, 0.8),
                admitted("reviewer-c", "trace:3", LearningSignalPolarity.NEUTRAL, 0.2)), policy());

        assertThat(result.outcome()).isEqualTo(FeedbackAggregation.Outcome.CONSENSUS_POSITIVE);
        assertThat(result.positiveRatio()).isGreaterThan(0.7);
        assertThat(result.distinctSources()).isEqualTo(3);
    }

    @Test
    void reachesNegativeConsensus() {
        var result = FeedbackAggregation.aggregate("agent:planner", List.of(
                admitted("reviewer-a", "trace:1", LearningSignalPolarity.NEGATIVE, 0.9),
                admitted("reviewer-b", "trace:2", LearningSignalPolarity.NEGATIVE, 0.8)), policy());

        assertThat(result.outcome()).isEqualTo(FeedbackAggregation.Outcome.CONSENSUS_NEGATIVE);
    }

    @Test
    void marksOpposingTrustedJudgmentsAsConflicted() {
        var result = FeedbackAggregation.aggregate("agent:planner", List.of(
                admitted("reviewer-a", "trace:1", LearningSignalPolarity.POSITIVE, 0.9),
                admitted("reviewer-b", "trace:2", LearningSignalPolarity.NEGATIVE, 0.8)), policy());

        assertThat(result.outcome()).isEqualTo(FeedbackAggregation.Outcome.CONFLICTED);
        assertThat(result.reasons()).anyMatch(reason -> reason.contains("opposing"));
    }

    @Test
    void refusesToTreatMixedOrNeutralFeedbackAsPositive() {
        var result = FeedbackAggregation.aggregate("agent:planner", List.of(
                admitted("reviewer-a", "trace:1", LearningSignalPolarity.MIXED, 0.9),
                admitted("reviewer-b", "trace:2", LearningSignalPolarity.NEUTRAL, 0.8)), policy());

        assertThat(result.outcome()).isEqualTo(FeedbackAggregation.Outcome.INSUFFICIENT_EVIDENCE);
        assertThat(result.positiveRatio()).isZero();
    }

    @Test
    void rejectsDuplicateEvidenceAcrossSignals() {
        var result = FeedbackAggregation.aggregate("agent:planner", List.of(
                admitted("reviewer-a", "trace:shared", LearningSignalPolarity.POSITIVE, 0.9),
                admitted("reviewer-b", "trace:shared", LearningSignalPolarity.POSITIVE, 0.8)), policy());

        assertThat(result.outcome()).isEqualTo(FeedbackAggregation.Outcome.CONFLICTED);
        assertThat(result.reasons()).anyMatch(reason -> reason.contains("reused"));
    }

    @Test
    void requiresMinimumSourcesAndUniqueEvidence() {
        var result = FeedbackAggregation.aggregate("agent:planner", List.of(
                admitted("reviewer-a", "trace:1", LearningSignalPolarity.POSITIVE, 0.9)), policy());

        assertThat(result.outcome()).isEqualTo(FeedbackAggregation.Outcome.INSUFFICIENT_EVIDENCE);
        assertThat(result.reasons()).anyMatch(reason -> reason.contains("distinct"));
    }

    @Test
    void refusesUnadmittedOrAutomatedFeedback() {
        LearningSignal automated = new LearningSignal("evaluation", LearningSignalType.EVALUATION,
                "judge", "execution", Optional.empty(), Optional.empty(), Optional.empty(),
                LearningSignalPolarity.POSITIVE, List.of("trace"), Optional.empty(), null, Map.of());
        assertThatThrownBy(() -> new FeedbackAggregation.AdmittedFeedback(automated,
                new FeedbackTrustPolicy.Decision(true, 1.0, List.of())))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new FeedbackAggregation.AdmittedFeedback(
                admitted("reviewer-a", "trace", LearningSignalPolarity.POSITIVE, 0.9).signal(),
                new FeedbackTrustPolicy.Decision(false, 0.0, List.of("rejected"))))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
