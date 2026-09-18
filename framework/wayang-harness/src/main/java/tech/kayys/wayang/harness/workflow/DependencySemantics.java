package tech.kayys.wayang.harness.workflow;

/**
 * Enforcement semantics of a dependency edge.
 */
public enum DependencySemantics {
    HARD,
    SOFT,
    OPTIONAL,
    EXCLUSIVE
}
