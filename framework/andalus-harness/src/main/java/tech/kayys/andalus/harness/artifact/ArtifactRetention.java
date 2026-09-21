package tech.kayys.andalus.harness.artifact;

/**
 * Retention policy for artifacts produced by executions.
 */
public enum ArtifactRetention {
    EPHEMERAL,
    TEMPORARY,
    RETAINED,
    IMMUTABLE
}
