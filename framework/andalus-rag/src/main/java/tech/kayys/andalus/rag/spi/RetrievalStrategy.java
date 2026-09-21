package tech.kayys.andalus.rag.spi;

import tech.kayys.andalus.rag.model.RagChunk;
import tech.kayys.andalus.rag.model.RagScoredChunk;
import tech.kayys.andalus.rag.model.MultimodalRetrievalQuery;
import java.util.List;

/**
 * SPI: strategy for retrieving relevant chunks from a vector store.
 * Implementations live in andalus-rag-runtime.
 */
public interface RetrievalStrategy {
    List<RagScoredChunk> retrieve(MultimodalRetrievalQuery query, VectorStore store);
}
