package tech.kayys.andalus.rag.spi;

import tech.kayys.andalus.rag.model.RagChunk;

/** A single result returned by a vector search. Part of the andalus-rag SPI. */
public record VectorSearchHit(RagChunk payload, double score, String id) {}
