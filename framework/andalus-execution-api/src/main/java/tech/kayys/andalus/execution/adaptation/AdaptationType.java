package tech.kayys.andalus.execution.adaptation;

/**
 * Kind of adaptation action proposed by the engine.
 */
public enum AdaptationType {
    RESOURCE_RESIZE,
    NODE_MIGRATION,
    STRATEGY_FALLBACK,
    BUDGET_THROTTLE,
    FAILOVER,
    PAUSE_FOR_APPROVAL
}
