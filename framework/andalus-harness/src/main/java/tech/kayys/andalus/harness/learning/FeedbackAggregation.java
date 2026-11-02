package tech.kayys.andalus.harness.learning;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Deterministic, advisory aggregation of independently admitted human feedback.
 * It never mutates a model, routing policy, or production configuration.
 */
public final class FeedbackAggregation {

    private FeedbackAggregation() {
    }

    public enum Outcome {
        CONSENSUS_POSITIVE,
        CONSENSUS_NEGATIVE,
        CONFLICTED,
        INSUFFICIENT_EVIDENCE
    }

    public record Policy(int minimumDistinctSources, int minimumEvidenceReferences,
                         double consensusRatio) {
        public Policy {
            if (minimumDistinctSources < 1 || minimumEvidenceReferences < 1) {
                throw new IllegalArgumentException("minimum source and evidence counts must be positive");
            }
            if (!Double.isFinite(consensusRatio) || consensusRatio <= 0.5 || consensusRatio > 1.0) {
                throw new IllegalArgumentException("consensusRatio must be in (0.5,1]");
            }
        }
    }

    /**
     * A feedback item paired with the result of FeedbackTrustPolicy.assess.
     * Unaccepted feedback cannot be represented as admitted evidence.
     */
    public static final class AdmittedFeedback {
        private final LearningSignal signal;
        private final FeedbackTrustPolicy.Decision admission;

        private AdmittedFeedback(LearningSignal signal, FeedbackTrustPolicy.Decision admission) {
            this.signal = signal;
            this.admission = admission;
        }

        public LearningSignal signal() {
            return signal;
        }

        public FeedbackTrustPolicy.Decision admission() {
            return admission;
        }
    }

    /**
     * Creates an aggregation input only after the configured trust policy accepts it.
     * The caller remains responsible for atomically persisting consumed evidence references.
     */
    public static Optional<AdmittedFeedback> admit(
            LearningSignal signal,
            FeedbackTrustPolicy.Policy trustPolicy,
            Set<String> previouslyUsedEvidenceReferences,
            Set<String> corroboratingSources) {
        FeedbackTrustPolicy.Decision decision = FeedbackTrustPolicy.assess(
                signal, trustPolicy, previouslyUsedEvidenceReferences, corroboratingSources);
        return decision.accepted()
                ? Optional.of(new AdmittedFeedback(signal, decision))
                : Optional.empty();
    }

    /**
     * Admits feedback only after trust checks pass and the evidence store
     * atomically reserves every evidence reference. The store is the replay
     * protection authority; callers must use a durable shared implementation
     * when multiple processes or restarts are involved.
     */
    public record AdmissionResult(Optional<AdmittedFeedback> feedback, List<String> reasons) {
        public AdmissionResult {
            feedback = feedback == null ? Optional.empty() : feedback;
            reasons = reasons == null ? List.of() : List.copyOf(reasons);
        }

        public boolean accepted() {
            return feedback.isPresent();
        }
    }

    /**
     * Trust-checks and atomically reserves evidence before creating an aggregation input.
     * The evidence store must be shared and durable when multiple processes ingest feedback.
     */
    public static AdmissionResult admitAndReserve(
            LearningSignal signal,
            FeedbackTrustPolicy.Policy trustPolicy,
            Set<String> corroboratingSources,
            FeedbackEvidenceStore evidenceStore) {
        if (signal == null || trustPolicy == null || evidenceStore == null) {
            throw new IllegalArgumentException("signal, trustPolicy, and evidenceStore are required");
        }

        FeedbackTrustPolicy.Decision decision = FeedbackTrustPolicy.assess(
                signal, trustPolicy, Set.of(), corroboratingSources);
        if (!decision.accepted()) {
            return new AdmissionResult(Optional.empty(), decision.reasons());
        }

        FeedbackEvidenceStore.Reservation reservation = evidenceStore.reserve(
                signal.id(), signal.evidenceReferences());
        if (!reservation.accepted()) {
            return new AdmissionResult(Optional.empty(),
                    List.of("evidence reservation rejected: " + reservation.status()));
        }
        return new AdmissionResult(Optional.of(new AdmittedFeedback(signal, decision)), List.of());
    }

    public record Result(String subject, Outcome outcome, double positiveWeight,
                         double negativeWeight, double ambiguousWeight,
                         double positiveRatio, double negativeRatio,
                         int distinctSources, int evidenceReferences, List<String> reasons) {
        public Result {
            if (subject == null || subject.isBlank() || outcome == null) {
                throw new IllegalArgumentException("subject and outcome are required");
            }
            reasons = reasons == null ? List.of() : List.copyOf(reasons);
        }
    }

    /**
     * Aggregates one explicitly identified subject. Callers must not combine
     * unrelated criteria; subject is a stable domain key, not free-form feedback text.
     */
    public static Result aggregate(String subject, List<AdmittedFeedback> feedback, Policy policy) {
        if (subject == null || subject.isBlank() || feedback == null || policy == null) {
            throw new IllegalArgumentException("subject, feedback, and policy are required");
        }

        Set<String> sources = new LinkedHashSet<>();
        Set<String> evidence = new LinkedHashSet<>();
        List<String> reasons = new ArrayList<>();
        double positive = 0.0;
        double negative = 0.0;
        double ambiguous = 0.0;
        double total = 0.0;
        boolean duplicateEvidence = false;

        for (AdmittedFeedback item : feedback) {
            if (item == null) {
                throw new IllegalArgumentException("feedback entries cannot be null");
            }
            LearningSignal signal = item.signal();
            sources.add(signal.source());
            for (String reference : signal.evidenceReferences()) {
                if (reference == null || reference.isBlank()) {
                    reasons.add("blank evidence reference encountered");
                    duplicateEvidence = true;
                } else if (!evidence.add(reference)) {
                    duplicateEvidence = true;
                    reasons.add("evidence reference is reused across feedback signals");
                }
            }

            double weight = item.admission().trustScore();
            if (!Double.isFinite(weight) || weight <= 0.0) {
                reasons.add("admitted feedback has no positive trust weight");
                continue;
            }
            total += weight;
            switch (signal.polarity()) {
                case POSITIVE -> positive += weight;
                case NEGATIVE -> negative += weight;
                case MIXED, NEUTRAL, UNKNOWN -> ambiguous += weight;
            }
        }

        if (duplicateEvidence) {
            return result(subject, Outcome.CONFLICTED, positive, negative, ambiguous, total,
                    sources.size(), evidence.size(), reasons);
        }
        if (sources.size() < policy.minimumDistinctSources()) {
            reasons.add("insufficient distinct trusted feedback sources");
        }
        if (evidence.size() < policy.minimumEvidenceReferences()) {
            reasons.add("insufficient unique evidence references");
        }
        if (total <= 0.0) {
            reasons.add("no positive trust-weighted feedback available");
        }
        if (!reasons.isEmpty()) {
            return result(subject, Outcome.INSUFFICIENT_EVIDENCE, positive, negative, ambiguous, total,
                    sources.size(), evidence.size(), reasons);
        }

        double positiveRatio = positive / total;
        double negativeRatio = negative / total;
        if (positiveRatio >= policy.consensusRatio()) {
            return result(subject, Outcome.CONSENSUS_POSITIVE, positive, negative, ambiguous, total,
                    sources.size(), evidence.size(), List.of());
        }
        if (negativeRatio >= policy.consensusRatio()) {
            return result(subject, Outcome.CONSENSUS_NEGATIVE, positive, negative, ambiguous, total,
                    sources.size(), evidence.size(), List.of());
        }
        if (positive > 0.0 && negative > 0.0) {
            return result(subject, Outcome.CONFLICTED, positive, negative, ambiguous, total,
                    sources.size(), evidence.size(), List.of("trusted feedback contains unresolved opposing judgments"));
        }
        return result(subject, Outcome.INSUFFICIENT_EVIDENCE, positive, negative, ambiguous, total,
                sources.size(), evidence.size(), List.of("ambiguous feedback does not establish directional consensus"));
    }

    private static Result result(String subject, Outcome outcome, double positive, double negative,
                                 double ambiguous, double total, int sources, int evidence, List<String> reasons) {
        return new Result(subject, outcome, positive, negative, ambiguous,
                total > 0.0 ? positive / total : 0.0,
                total > 0.0 ? negative / total : 0.0,
                sources, evidence, reasons);
    }
}
