package tech.kayys.wayang.workflow.graph;

/**
 * Standard classification of execution nodes in a graph.
 */
public enum NodeType {
    OPERATION,
    CONDITION,
    CHECKPOINT,
    JOIN,
    SPLIT,
    DELEGATION,
    TERMINAL
}
