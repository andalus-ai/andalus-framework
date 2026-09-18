package tech.kayys.wayang.knowledge.audit;

import java.util.List;

/**
 * Defines the contract for knowledge audit store operations in the Wayang framework.
 */


public interface KnowledgeAuditStore extends KnowledgeAuditSink {

    List<KnowledgeAuditEvent> query(KnowledgeAuditQuery query);
}
