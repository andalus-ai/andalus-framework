package tech.kayys.andalus.knowledge.pipeline;

import tech.kayys.andalus.knowledge.KnowledgeEvidence;
import tech.kayys.andalus.knowledge.KnowledgeQuery;
import tech.kayys.andalus.knowledge.KnowledgeSelectionResult;

import java.util.List;
import java.util.Map;

/**
 * Result of executing a knowledge-centric RAG retrieval pipeline without answers/generation.
 */
public record KnowledgeRagResult(
        KnowledgeQuery query,
        List<KnowledgeEvidence> evidence,
        KnowledgeSelectionResult selection,
        Map<String, Object> diagnostics
) {

    public KnowledgeRagResult {
        evidence = evidence == null ? List.of() : List.copyOf(evidence);
        diagnostics = diagnostics == null ? Map.of() : Map.copyOf(diagnostics);
    }

    public static KnowledgeRagResult of(KnowledgeQuery query, List<KnowledgeEvidence> evidence, KnowledgeSelectionResult selection) {
        return new KnowledgeRagResult(query, evidence, selection, Map.of());
    }
}
