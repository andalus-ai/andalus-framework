package tech.kayys.andalus.knowledge.seal;

/**
 * Defines the knowledge snapshot trust anchor type values used by the Andalus framework.
 */


public enum KnowledgeSnapshotTrustAnchorType {
    LOCAL,
    ANDALUS_RUNTIME,
    KMS,
    HSM,
    PUBLIC_KEY,
    TIMESTAMP_AUTHORITY,
    TRANSPARENCY_LOG,
    EXTERNAL
}
