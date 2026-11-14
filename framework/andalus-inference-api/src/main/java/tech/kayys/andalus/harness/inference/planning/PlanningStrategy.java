package tech.kayys.andalus.harness.inference.planning;

/**
 * Strategy mode used by an inference planner.
 */
public enum PlanningStrategy {
    /**
     * Strictly honors explicit requested model and provider.
     */
    DIRECT,

    /**
     * Evaluates all feasible candidates and selects the optimal one according to configured multi-criteria.
     */
    ADAPTIVE
}
