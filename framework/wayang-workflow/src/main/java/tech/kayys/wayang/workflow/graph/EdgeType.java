package tech.kayys.wayang.workflow.graph;

/**
 * Semantic type of dependency or flow connecting two nodes in an execution graph.
 */
public enum EdgeType {
    DEPENDS_ON,
    DATA,
    CONDITION,
    ERROR,
    COMPENSATION,
    COMPLETION
}
