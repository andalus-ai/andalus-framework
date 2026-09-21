package tech.kayys.andalus.rag.spi;

import tech.kayys.andalus.rag.model.RagDocument;
import tech.kayys.andalus.rag.model.RagQuery;
import tech.kayys.andalus.rag.model.RagResult;
import java.util.List;

/**
 * SPI: top-level RAG pipeline contract.
 * Combines document ingestion + retrieval + generation.
 * Implementations live in andalus-rag-runtime.
 */
public interface RagPipeline {

    /** Ingest documents into this pipeline's vector store. */
    void ingest(List<RagDocument> documents);

    /** Execute a RAG query and return the result. */
    RagResult query(RagQuery query);

    /** Clear all documents from this pipeline's namespace. */
    default void clear(String namespace) {}
}
