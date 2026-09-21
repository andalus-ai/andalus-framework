package tech.kayys.andalus.knowledge.exchange.checkpoint;

/**
 * Defines the knowledge answer resolution checkpoint status values used by the Wayang framework.
 */


public enum KnowledgeAnswerResolutionCheckpointStatus {
    CREATED,
    VERIFIED,
    ACTIVE,
    SUPERSEDED,
    INVALID,
    CORRUPTED,
    INSTALLED,
    FAILED
}
