package tech.kayys.wayang.workflow.graph;

/**
 * Enforcement semantics of a dependency edge.
 */
public enum DependencySemantics {
    HARD,
    SOFT,
    OPTIONAL,
    EXCLUSIVE
}
