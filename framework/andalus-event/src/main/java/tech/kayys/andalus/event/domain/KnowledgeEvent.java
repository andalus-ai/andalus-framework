package tech.kayys.andalus.event.domain;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import tech.kayys.andalus.extension.Extension;
import tech.kayys.andalus.resource.Resource;
import tech.kayys.andalus.resource.BaseResource;


import java.util.List;

import tech.kayys.andalus.event.EventPayload;
import tech.kayys.andalus.resource.ResourceType.Document;

/**
 * Knowledge Events
 */
public interface KnowledgeEvent extends EventPayload {
    
    record RetrievalStarted(
        String knowledgeId,
        String query,
        String retrievalType
    ) implements KnowledgeEvent {}
    
    record RetrievalCompleted(
        String knowledgeId,
        List<Document> documents,
        int count,
        long durationMs
    ) implements KnowledgeEvent {}
    
    record RetrievalFailed(
        String knowledgeId,
        String error
    ) implements KnowledgeEvent {}
    
    record EmbeddingGenerated(
        String knowledgeId,
        String text,
        int dimension,
        long durationMs
    ) implements KnowledgeEvent {}
    
    record RerankFinished(
        String knowledgeId,
        int inputCount,
        int outputCount,
        long durationMs
    ) implements KnowledgeEvent {}
}