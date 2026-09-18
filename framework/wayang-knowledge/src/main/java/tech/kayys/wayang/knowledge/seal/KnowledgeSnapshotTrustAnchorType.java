package tech.kayys.wayang.knowledge.seal;

/**
 * Defines the knowledge snapshot trust anchor type values used by the Wayang framework.
 */


public enum KnowledgeSnapshotTrustAnchorType {
    LOCAL,
    WAYANG_RUNTIME,
    KMS,
    HSM,
    PUBLIC_KEY,
    TIMESTAMP_AUTHORITY,
    TRANSPARENCY_LOG,
    EXTERNAL
}
