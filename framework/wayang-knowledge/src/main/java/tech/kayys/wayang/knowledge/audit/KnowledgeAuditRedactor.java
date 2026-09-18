package tech.kayys.wayang.knowledge.audit;

/**
 * Defines the contract for knowledge audit redactor operations in the Wayang framework.
 */


public interface KnowledgeAuditRedactor {

    KnowledgeAuditEvent redact(KnowledgeAuditEvent event);
}
