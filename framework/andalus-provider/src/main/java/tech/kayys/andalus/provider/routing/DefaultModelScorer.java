package tech.kayys.andalus.provider.routing;

/**
 * Default multi-dimensional scoring implementation balancing quality, cost, latency, and health.
 *
 * Runtime observations refine catalog estimates only after a minimum sample count. They never
 * relax the hard constraints applied by AdaptiveModelRouter.
 */
public class DefaultModelScorer implements ModelScorer {

    private static final long MIN_LATENCY_SAMPLES = 3;

    @Override
    public InferencePlan.ScoredCandidate score(
            ModelSpec spec,
            InferenceRequirements requirements,
            InferencePolicy policy,
            ModelRoutingTelemetry telemetry
    ) {
        // 1. Quality Component (0.0 to 1.0)
        double quality = clamp(spec.qualityScore(), 0.0, 1.0);
        if (requirements.requiresReasoning() && spec.supportsReasoning()) {
            quality = Math.min(1.0, quality + 0.15);
        }

        // 2. Cost Component (0.0 to 1.0)
        long estimatedInputTokens = Math.max(requirements.requiredContextTokens(), 2048);
        long estimatedOutputTokens = Math.max(1, spec.maxOutputTokens() > 0
                ? Math.min(1024, spec.maxOutputTokens()) : 1024);
        double estimatedCost = spec.estimateCost(estimatedInputTokens, estimatedOutputTokens);

        double costScore;
        if (spec.isLocal() || estimatedCost <= 0.0) {
            costScore = 1.0;
        } else if (!Double.isFinite(estimatedCost)) {
            costScore = 0.0;
        } else {
            costScore = 1.0 / (1.0 + estimatedCost * 10.0);
        }

        // 3. Latency Component (0.0 to 1.0)
        long latencyMs = Math.max(1, spec.latencyP50Ms());
        if (telemetry != null) {
            ModelRoutingTelemetry.ModelStats stats = telemetry.getStats(spec.modelId());
            if (stats != null && stats.totalCalls.get() >= MIN_LATENCY_SAMPLES) {
                long observedLatency = stats.averageLatencyMs();
                if (observedLatency > 0) {
                    latencyMs = observedLatency;
                }
            }
        }
        double latencyScore = 1000.0 / (1000.0 + latencyMs);

        // 4. Objective Weights
        double wQuality;
        double wCost;
        double wLatency;
        double localBonus = 0.0;

        switch (policy.objective()) {
            case COST_MINIMIZING -> {
                wQuality = 0.15;
                wCost = 0.65;
                wLatency = 0.20;
            }
            case QUALITY_FIRST -> {
                wQuality = 0.85;
                wCost = 0.05;
                wLatency = 0.10;
            }
            case LATENCY_FIRST -> {
                wQuality = 0.20;
                wCost = 0.15;
                wLatency = 0.65;
            }
            case LOCAL_FIRST -> {
                wQuality = 0.25;
                wCost = 0.40;
                wLatency = 0.35;
                if (spec.isLocal()) localBonus = 0.30;
            }
            case BALANCED -> {
                wQuality = 0.40;
                wCost = 0.35;
                wLatency = 0.25;
            }
            default -> {
                wQuality = 0.34;
                wCost = 0.33;
                wLatency = 0.33;
            }
        }

        double baseScore = (wQuality * quality) + (wCost * costScore)
                + (wLatency * latencyScore) + localBonus;

        // 5. Health Multiplier from Telemetry. Insufficient samples are neutral.
        double healthMultiplier = telemetry != null
                ? clamp(telemetry.getHealthMultiplier(spec.modelId()), 0.0, 1.0)
                : 1.0;
        double totalScore = baseScore * healthMultiplier;

        return new InferencePlan.ScoredCandidate(
                spec,
                totalScore,
                quality,
                costScore,
                latencyScore,
                healthMultiplier
        );
    }

    private static double clamp(double value, double min, double max) {
        if (!Double.isFinite(value)) return min;
        return Math.max(min, Math.min(max, value));
    }
}
