package tech.kayys.wayang.harness.artifact;

/**
 * Causal and logical relationships between artifacts in the lineage graph.
 */
public enum ArtifactRelation {
    DERIVED_FROM,
    GENERATED_FROM,
    TRANSFORMED_FROM,
    PATCHES,
    TESTED_BY,
    BUILT_FROM,
    CONTAINS,
    REFERENCES,
    SNAPSHOT_OF
}
