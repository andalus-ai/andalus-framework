package tech.kayys.andalus.execution.resource.graph;

/**
 * Semantic relationships between virtual execution resources in a ResourceGraph.
 */
public enum ResourceRelation {
    REQUIRES,
    DEPENDS_ON,
    MOUNTED_ON,
    CONNECTED_TO,
    PROVIDED_BY,
    AUTHORIZED_BY,
    DERIVED_FROM
}
