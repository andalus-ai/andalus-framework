package tech.kayys.wayang.knowledge.seal;

/**
 * Defines the contract for knowledge snapshot trust anchor operations in the Wayang framework.
 */


public interface KnowledgeSnapshotTrustAnchor {

    KnowledgeSnapshotTrustAnchorType type();

    String anchorId();
}
