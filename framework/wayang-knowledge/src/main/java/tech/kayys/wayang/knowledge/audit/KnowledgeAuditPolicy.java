package tech.kayys.wayang.knowledge.audit;

import tech.kayys.wayang.knowledge.decision.KnowledgeDecisionTrace;

/**
 * Defines the contract for knowledge audit policy operations in the Wayang framework.
 */


public interface KnowledgeAuditPolicy {

    boolean shouldAudit(KnowledgeDecisionTrace trace);
}
