package tech.kayys.wayang.knowledge.lineage;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Defines the contract for knowledge lineage store operations in the Wayang framework.
 */


public interface KnowledgeLineageStore {

    CompletionStage<KnowledgeLineageEdge> save(KnowledgeLineageEdge edge);

    CompletionStage<List<KnowledgeLineageEdge>> getEdgesFor(String nodeId);

    CompletionStage<List<KnowledgeLineageEdge>> getAncestors(String nodeId, int maxDepth);

    CompletionStage<EvidenceBundle> saveBundle(EvidenceBundle bundle);
}
