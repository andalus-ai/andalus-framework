package tech.kayys.andalus.harness.decision.analytics;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * An evidence-backed finding produced by the decision analytics layer (Phase 4O).
 *
 * <p>Every finding carries the explicit population and denominator used to derive it
 * so that consumers can assess completeness. A finding describes a pattern; it does
 * not automatically trigger any policy, memory, or configuration change.
 *
 * <p>Maps to the {@code DecisionAnalyticsFinding} concept described in §4O.5 of the spec,
 * and is compatible with the existing {@code AnalyticsInsight} pattern.
 */
public record DecisionAnalyticsFinding(
        String id,
        String metricId,
        String category,
        String description,
        long population,
        long denominator,
        Map<String, Object> observedValues,
        List<String> evidenceReferences,
        Map<String, Object> limitations,
        boolean dataComplete,
        Instant generatedAt
) {

    public DecisionAnalyticsFinding {
        id = id == null || id.isBlank() ? "daf-" + UUID.randomUUID() : id;
        Objects.requireNonNull(metricId, "metricId must not be null");
        category = Objects.requireNonNullElse(category, "GENERAL");
        description = Objects.requireNonNullElse(description, "");
        observedValues = observedValues == null ? Map.of() : Map.copyOf(observedValues);
        evidenceReferences = evidenceReferences == null ? List.of() : List.copyOf(evidenceReferences);
        limitations = limitations == null ? Map.of() : Map.copyOf(limitations);
        generatedAt = generatedAt == null ? Instant.now() : generatedAt;
    }

    /** Creates a complete finding with all required fields. */
    public static DecisionAnalyticsFinding of(
            String metricId,
            String category,
            String description,
            long population,
            long denominator,
            Map<String, Object> observedValues,
            List<String> evidenceReferences) {

        return new DecisionAnalyticsFinding(
                null, metricId, category, description,
                population, denominator, observedValues,
                evidenceReferences, Map.of(), true, Instant.now());
    }

    /** Creates a partial finding that explicitly signals incomplete telemetry. */
    public static DecisionAnalyticsFinding incomplete(
            String metricId,
            String category,
            String description,
            Map<String, Object> limitations) {

        return new DecisionAnalyticsFinding(
                null, metricId, category, description,
                0, 0, Map.of(), List.of(), limitations, false, Instant.now());
    }

    /** Returns the ratio (population / denominator) or {@link Optional#empty()} if denominator is zero. */
    public Optional<Double> rate() {
        if (denominator == 0) return Optional.empty();
        return Optional.of((double) population / denominator);
    }
}
