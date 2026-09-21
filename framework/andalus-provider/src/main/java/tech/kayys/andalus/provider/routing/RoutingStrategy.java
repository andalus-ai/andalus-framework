package tech.kayys.andalus.provider.routing;

/**
 * Strategy mode for model routing in Andalus.
 */
public enum RoutingStrategy {
    /** Direct, strict model routing based on explicit requested model without budget/telemetry scoring. */
    DIRECT,

    /** Multi-dimensional, budget-aware, capability-filtered, and telemetry-adaptive model routing. */
    ADAPTIVE
}
