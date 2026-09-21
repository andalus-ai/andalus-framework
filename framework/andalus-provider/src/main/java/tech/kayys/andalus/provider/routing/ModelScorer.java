package tech.kayys.andalus.provider.routing;

/**
 * Multi-criteria scorer for evaluating candidate models against requirements and policy.
 */
public interface ModelScorer {

    InferencePlan.ScoredCandidate score(
            ModelSpec spec,
            InferenceRequirements requirements,
            InferencePolicy policy,
            ModelRoutingTelemetry telemetry
    );
}
