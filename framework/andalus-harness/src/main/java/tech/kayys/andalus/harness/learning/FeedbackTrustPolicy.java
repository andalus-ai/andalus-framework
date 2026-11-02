package tech.kayys.andalus.harness.learning;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Deterministic admission policy for human feedback before it is considered
 * as adaptation evidence. This policy does not ingest signals or change models.
 */
public final class FeedbackTrustPolicy {

    private FeedbackTrustPolicy() {
    }

    /**
     * Exact source identifiers and their operator-assigned trust weights.
     * Unknown sources receive no trust and are never implicitly promoted.
     */
    public record Policy(
            Map<String, Double> sourceTrust,
            double minimumSourceTrust,
            int minimumEvidenceReferences,
            int minimumDistinctTrustedSources
    ) {
        public Policy {
            sourceTrust = sourceTrust == null ? Map.of() : Map.copyOf(sourceTrust);
            if (!Double.isFinite(minimumSourceTrust) || minimumSourceTrust < 0 || minimumSourceTrust > 1) {
                throw new IllegalArgumentException("minimumSourceTrust must be within [0,1]");
            }
            if (minimumEvidenceReferences < 1) {
                throw new IllegalArgumentException("minimumEvidenceReferences must be positive");
            }
            if (minimumDistinctTrustedSources < 1) {
                throw new IllegalArgumentException("minimumDistinctTrustedSources must be positive");
            }
            sourceTrust.forEach((source, trust) -> {
                if (source == null || source.isBlank() || trust == null
                        || !Double.isFinite(trust) || trust < 0 || trust > 1) {
                    throw new IllegalArgumentException("source trust entries must have a source and weight within [0,1]");
                }
            });
        }
    }

    public record Decision(boolean accepted, double trustScore, List<String> reasons) {
        public Decision {
            if (!Double.isFinite(trustScore) || trustScore < 0 || trustScore > 1) {
                throw new IllegalArgumentException("trustScore must be within [0,1]");
            }
            reasons = reasons == null ? List.of() : List.copyOf(reasons);
        }
    }

    /**
     * Assesses one human-feedback signal using caller-supplied evidence history
     * and independently established corroborating source identities.
     *
     * @param signal feedback signal to assess
     * @param previouslyUsedEvidenceReferences references already consumed by prior accepted feedback
     * @param corroboratingSources other independently verified reviewer/source identifiers
     */
    public static Decision assess(
            LearningSignal signal,
            Policy policy,
            Set<String> previouslyUsedEvidenceReferences,
            Set<String> corroboratingSources
    ) {
        if (signal == null || policy == null) {
            throw new IllegalArgumentException("signal and policy are required");
        }

        List<String> reasons = new ArrayList<>();
        if (signal.type() != LearningSignalType.HUMAN_FEEDBACK) {
            reasons.add("only HUMAN_FEEDBACK signals are eligible for this policy");
        }

        double sourceWeight = policy.sourceTrust().getOrDefault(signal.source(), 0.0);
        if (sourceWeight < policy.minimumSourceTrust()) {
            reasons.add("feedback source is untrusted or below the configured trust threshold");
        }

        Set<String> evidence = new LinkedHashSet<>(signal.evidenceReferences());
        if (evidence.size() != signal.evidenceReferences().size()) {
            reasons.add("signal contains duplicate evidence references");
        }
        if (evidence.stream().anyMatch(reference -> reference == null || reference.isBlank())) {
            reasons.add("signal contains a blank evidence reference");
        }
        if (evidence.size() < policy.minimumEvidenceReferences()) {
            reasons.add("insufficient independent evidence references");
        }

        Set<String> previouslyUsed = previouslyUsedEvidenceReferences == null
                ? Set.of() : Set.copyOf(previouslyUsedEvidenceReferences);
        if (evidence.stream().anyMatch(previouslyUsed::contains)) {
            reasons.add("evidence reference was already used by previously accepted feedback");
        }

        Set<String> sources = new LinkedHashSet<>();
        sources.add(signal.source());
        if (corroboratingSources != null) {
            sources.addAll(corroboratingSources);
        }
        long trustedSourceCount = sources.stream()
                .filter(source -> source != null
                        && policy.sourceTrust().getOrDefault(source, 0.0) >= policy.minimumSourceTrust())
                .distinct()
                .count();
        if (trustedSourceCount < policy.minimumDistinctTrustedSources()) {
            reasons.add("insufficient distinct trusted sources for corroboration");
        }

        return new Decision(reasons.isEmpty(), sourceWeight, reasons);
    }
}
