package tech.kayys.wayang.knowledge.audit;

/**
 * Provides no op knowledge audit redactor behavior for the Wayang framework.
 */


public final class NoOpKnowledgeAuditRedactor implements KnowledgeAuditRedactor {

    @Override
    public KnowledgeAuditEvent redact(KnowledgeAuditEvent event) {
        return event;
    }
}
