package tech.kayys.andalus.execution.economics;

/**
 * Policy enforced when a budget limit is reached.
 */
public enum BudgetPolicy {
    STRICT_HALT,
    NOTIFY_AND_THROTTLE,
    FAILOVER_TO_CHEAPER_PROVIDER
}
