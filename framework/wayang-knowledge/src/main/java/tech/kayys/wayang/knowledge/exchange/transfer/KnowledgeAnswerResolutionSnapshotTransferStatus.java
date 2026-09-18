package tech.kayys.wayang.knowledge.exchange.transfer;

/**
 * Defines the knowledge answer resolution snapshot transfer status values used by the Wayang framework.
 */


public enum KnowledgeAnswerResolutionSnapshotTransferStatus {
    DISCOVERED,
    REQUESTED,
    TRANSFERRING,
    PAUSED,
    COMPLETED,
    VERIFIED,
    INSTALLED,
    CANCELLED,
    EXPIRED,
    FAILED,
    BLOCKED
}
