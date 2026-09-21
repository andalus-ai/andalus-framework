package tech.kayys.andalus.knowledge.lineage;

import java.util.List;
import java.util.concurrent.CompletionStage;

/**
 * Service managing lineage inspection and explainability queries.
 */
public interface KnowledgeLineageService {

    CompletionStage<List<KnowledgeLineageEdge>> traceProvenance(String knowledgeItemId);

    CompletionStage<EvidenceBundle> createBundle(String query, List<tech.kayys.andalus.knowledge.KnowledgeEvidence> evidence);
}
