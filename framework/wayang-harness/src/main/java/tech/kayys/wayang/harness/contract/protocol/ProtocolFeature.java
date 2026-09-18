package tech.kayys.wayang.harness.contract.protocol;

/**
 * Extensible protocol capabilities negotiated between Wayang and agents.
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
