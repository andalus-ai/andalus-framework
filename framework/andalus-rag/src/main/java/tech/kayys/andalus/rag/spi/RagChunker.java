package tech.kayys.andalus.rag.spi;

import tech.kayys.andalus.rag.model.RagChunk;
import tech.kayys.andalus.rag.model.RagDocument;
import java.util.List;

/**
 * SPI: document chunking strategy.
 * Splits a RagDocument into RagChunks before embedding.
 */
public interface RagChunker {
    List<RagChunk> chunk(RagDocument document);
}
