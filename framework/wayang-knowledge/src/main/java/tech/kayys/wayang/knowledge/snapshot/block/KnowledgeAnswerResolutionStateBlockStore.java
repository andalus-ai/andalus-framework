package tech.kayys.wayang.knowledge.snapshot.block;

import java.util.Optional;

/**
 * Defines the contract for knowledge answer resolution state block store operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionStateBlockStore {

    KnowledgeAnswerResolutionStateBlock put(KnowledgeAnswerResolutionStateBlock block);

    Optional<KnowledgeAnswerResolutionStateBlock> get(String blockId);

    boolean contains(String blockId);

    void delete(String blockId);
}
