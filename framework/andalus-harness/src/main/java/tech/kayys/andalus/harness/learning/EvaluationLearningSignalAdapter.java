package tech.kayys.andalus.harness.learning;

import tech.kayys.andalus.harness.evaluation.Evaluator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Converts completed offline evaluation reports into attributable learning evidence.
 *
 * <p>This adapter only emits a signal candidate. It does not train, route, promote,
 * or deploy a model. Reports below the configured evidence floor are ignored.</p>
 */
public final class EvaluationLearningSignalAdapter {

    private EvaluationLearningSignalAdapter() {
    }

    /**
     * Evidence policy used to prevent small evaluations from influencing adaptation.
     *
     * @param minimumCases minimum number of evaluated cases
     * @param priorWeight positive prior weight used by the confidence estimate
     * @param minimumConfidence minimum confidence required to emit a signal
     */
    public record Policy(int minimumCases, double priorWeight, double minimumConfidence) {
        public Policy {
            if (minimumCases < 1) {
                throw new IllegalArgumentException("minimumCases must be positive");
            }
            if (!Double.isFinite(priorWeight) || priorWeight <= 0) {
                throw new IllegalArgumentException("priorWeight must be positive and finite");
            }
            if (!Double.isFinite(minimumConfidence) || minimumConfidence < 0 || minimumConfidence > 1) {
                throw new IllegalArgumentException("minimumConfidence must be within [0,1]");
            }
        }

        public static Policy conservativeDefault() {
            return new Policy(10, 10.0, 0.5);
        }
    }

    /**
     * Returns empty when the report does not meet the evidence policy.
     * Metric values and evaluator provenance are copied as scalar metadata only;
     * case inputs, expected outputs, and free-form evaluator details are excluded.
     */
    public static Optional<LearningSignal> adapt(Evaluator.Report report, Policy policy) {
        if (report == null) {
            throw new IllegalArgumentException("report is required");
        }
        if (policy == null) {
            throw new IllegalArgumentException("policy is required");
        }
        int cases = report.results().size();
        if (cases < policy.minimumCases()) {
            return Optional.empty();
        }

        double confidence = cases / (cases + policy.priorWeight());
        if (confidence < policy.minimumConfidence()) {
            return Optional.empty();
        }

        LearningSignalPolarity polarity;
        if (report.passRate() >= 1.0) {
            polarity = LearningSignalPolarity.POSITIVE;
        } else if (report.passRate() <= 0.0) {
            polarity = LearningSignalPolarity.NEGATIVE;
        } else {
            polarity = LearningSignalPolarity.MIXED;
        }

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("evaluationId", report.evaluationId());
        metadata.put("candidateId", report.candidateId());
        metadata.put("candidateVersion", report.candidateVersion());
        metadata.put("datasetId", report.datasetId());
        metadata.put("datasetVersion", report.datasetVersion());
        metadata.put("evaluatorId", report.evaluatorId());
        metadata.put("evaluatorVersion", report.evaluatorVersion());
        metadata.put("evaluatorConfigFingerprint", report.evaluatorConfigFingerprint());
        metadata.put("caseCount", cases);
        metadata.put("passedCases", report.passedCases());
        metadata.put("failedCases", report.failedCases());
        metadata.put("passRate", report.passRate());
        metadata.put("confidence", confidence);
        metadata.put("aggregateMetrics", Map.copyOf(report.aggregateMetrics()));

        return Optional.of(new LearningSignal(
                null,
                LearningSignalType.EVALUATION,
                "evaluation:" + report.evaluatorId(),
                report.evaluationId(),
                Optional.empty(),
                Optional.empty(),
                Optional.empty(),
                polarity,
                List.of("evaluation:" + report.evaluationId()),
                Optional.of(report.evaluationId()),
                report.completedAt(),
                metadata
        ));
    }
}
