package tech.kayys.wayang.knowledge.replay;

import tech.kayys.wayang.knowledge.decision.KnowledgeDecisionTrace;

/**
 * Defines the contract for knowledge decision reexecutor operations in the Wayang framework.
 */


public interface KnowledgeDecisionReexecutor {

    KnowledgeDecisionTrace execute(
            KnowledgeReplaySnapshot snapshot,
            KnowledgeReplayContext context);
}
