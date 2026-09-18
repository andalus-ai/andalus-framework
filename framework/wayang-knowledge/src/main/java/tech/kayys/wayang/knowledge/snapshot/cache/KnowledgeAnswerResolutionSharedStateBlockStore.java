package tech.kayys.wayang.knowledge.snapshot.cache;

import java.util.Optional;

/**
 * Defines the contract for knowledge answer resolution shared state block store operations in the Wayang framework.
 */


public interface KnowledgeAnswerResolutionSharedStateBlockStore {

    KnowledgeAnswerResolutionSharedStateBlock put(KnowledgeAnswerResolutionSharedStateBlock block);

    Optional<KnowledgeAnswerResolutionSharedStateBlock> get(KnowledgeAnswerResolutionStateBlockId id);

    boolean contains(KnowledgeAnswerResolutionStateBlockId id);

    void delete(KnowledgeAnswerResolutionStateBlockId id);
}
