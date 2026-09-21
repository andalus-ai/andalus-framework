package tech.kayys.andalus.knowledge.decision;

import java.util.List;
import java.util.Optional;

/**
 * Defines the contract for knowledge decision trace service operations in the Andalus framework.
 */


public interface KnowledgeDecisionTraceService {

    void record(KnowledgeDecisionTrace trace);

    Optional<KnowledgeDecisionTrace> get(String traceId);

    List<KnowledgeDecisionTrace> findByExecution(String executionId);

    List<KnowledgeDecisionTrace> findByAgent(String agentId);

    List<KnowledgeDecisionTrace> findByEvidence(String evidenceId);
}
