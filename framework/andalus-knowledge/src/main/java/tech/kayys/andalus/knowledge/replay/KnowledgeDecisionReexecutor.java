package tech.kayys.andalus.knowledge.replay;

import tech.kayys.andalus.knowledge.decision.KnowledgeDecisionTrace;

/**
 * Defines the contract for knowledge decision reexecutor operations in the Andalus framework.
 */


public interface KnowledgeDecisionReexecutor {

    KnowledgeDecisionTrace execute(
            KnowledgeReplaySnapshot snapshot,
            KnowledgeReplayContext context);
}
