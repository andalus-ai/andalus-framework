package tech.kayys.andalus.rag.spi;

import tech.kayys.andalus.rag.model.RagChunk;
import java.util.List;

/**
 * SPI: embedding service for converting text to float vectors.
 */
public interface RagEmbedder {
    float[] embed(String text);
    List<float[]> embedAll(List<String> texts);
    int dimensions();
}
