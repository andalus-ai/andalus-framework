package tech.kayys.andalus.harness.contract.protocol;

/**
 * Extensible protocol capabilities negotiated between Andalus and agents.
 */
public enum ProtocolFeature {
    STREAMING,
    CANCELLATION,
    PAUSE_RESUME,
    CONTEXT_EXCHANGE,
    DELEGATION,
    ARTIFACT_EXCHANGE,
    PROGRESS_REPORTING,
    BACKPRESSURE
}
