package tech.kayys.wayang.knowledge.replay;

/**
 * Defines the knowledge replay status values used by the Wayang framework.
 */


public enum KnowledgeReplayStatus {
    REPRODUCED,
    DIVERGED,
    INCOMPLETE,
    BLOCKED,
    FAILED
}
