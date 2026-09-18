package tech.kayys.wayang.knowledge.replay;

/**
 * Defines the contract for knowledge replay audit sink operations in the Wayang framework.
 */


public interface KnowledgeReplayAuditSink {

    void publish(KnowledgeReplayAuditEvent event);
}
