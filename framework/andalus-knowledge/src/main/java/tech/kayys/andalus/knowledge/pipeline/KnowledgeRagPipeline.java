package tech.kayys.andalus.knowledge.pipeline;

import tech.kayys.andalus.knowledge.RetrievalContext;

import java.util.concurrent.CompletionStage;

/**
 * Top-level retrieval pipeline decoupled from model answer generation.
 */
public interface KnowledgeRagPipeline {

    CompletionStage<KnowledgeRagResult> retrieve(RetrievalContext context);
}
