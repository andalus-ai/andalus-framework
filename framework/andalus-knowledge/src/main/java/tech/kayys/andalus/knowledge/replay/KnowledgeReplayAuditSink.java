package tech.kayys.andalus.knowledge.replay;

/**
 * Defines the contract for knowledge replay audit sink operations in the Andalus framework.
 */


public interface KnowledgeReplayAuditSink {

    void publish(KnowledgeReplayAuditEvent event);
}
