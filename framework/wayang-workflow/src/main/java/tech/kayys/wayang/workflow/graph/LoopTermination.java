package tech.kayys.wayang.workflow.graph;

/**
 * Termination reasons for bounded execution loops.
 */
public enum LoopTermination {
    CONDITION_MET,
    MAX_ITERATIONS,
    BUDGET_EXHAUSTED,
    TIMEOUT
}
