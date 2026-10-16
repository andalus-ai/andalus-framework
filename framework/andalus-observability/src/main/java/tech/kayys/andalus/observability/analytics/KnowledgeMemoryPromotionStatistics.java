package tech.kayys.andalus.observability.analytics;

import java.util.Map;

/**
 * Derived operational statistics for knowledge-to-memory promotions.
 */
public record KnowledgeMemoryPromotionStatistics(
        long evidenceSelected,
        long promotionEvaluated,
        long promotionAllowed,
        long promotionDenied,
        long candidatesCreated,
        long candidatesAdmitted,
        Map<String, Long> denialReasons,
        Map<String, Long> admissionReasons
) {

    public KnowledgeMemoryPromotionStatistics {
        denialReasons = denialReasons == null ? Map.of() : Map.copyOf(denialReasons);
        admissionReasons = admissionReasons == null ? Map.of() : Map.copyOf(admissionReasons);
    }
}
