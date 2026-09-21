package tech.kayys.andalus.knowledge.seal;

/**
 * Defines the contract for knowledge snapshot trust anchor operations in the Andalus framework.
 */


public interface KnowledgeSnapshotTrustAnchor {

    KnowledgeSnapshotTrustAnchorType type();

    String anchorId();
}
