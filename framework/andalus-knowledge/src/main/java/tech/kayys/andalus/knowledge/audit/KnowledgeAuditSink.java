package tech.kayys.andalus.knowledge.audit;

/**
 * Destination for knowledge audit events.
 */
public interface KnowledgeAuditSink {

    void publish(KnowledgeAuditEvent event);
}
