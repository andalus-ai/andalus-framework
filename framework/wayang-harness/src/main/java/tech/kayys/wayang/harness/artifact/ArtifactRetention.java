package tech.kayys.wayang.harness.artifact;

/**
 * Retention policy for artifacts produced by executions.
 */
public enum ArtifactRetention {
    EPHEMERAL,
    TEMPORARY,
    RETAINED,
    IMMUTABLE
}
