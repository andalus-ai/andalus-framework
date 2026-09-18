package tech.kayys.wayang.harness.information.knowledge;

/**
 * Universal SPI for retrieving factual evidence from knowledge bases and indexes.
 */
public interface KnowledgeRetriever {

    RetrievalResult retrieve(KnowledgeSourceId sourceId, String query, int limit);
}
