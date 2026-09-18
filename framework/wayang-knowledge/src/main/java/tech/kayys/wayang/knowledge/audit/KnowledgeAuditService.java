package tech.kayys.wayang.knowledge.audit;

import tech.kayys.wayang.knowledge.decision.KnowledgeDecisionTrace;

import java.util.List;

/**
 * Defines the contract for knowledge audit service operations in the Wayang framework.
 */


public interface KnowledgeAuditService {

    void audit(KnowledgeAuditEvent event);

    void audit(KnowledgeDecisionTrace trace, KnowledgeAuditContext context);

    List<KnowledgeAuditEvent> query(KnowledgeAuditQuery query);
}
