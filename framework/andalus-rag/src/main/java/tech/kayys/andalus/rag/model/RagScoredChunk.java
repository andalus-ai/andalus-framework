package tech.kayys.andalus.rag.model;

/** A retrieved chunk with its similarity score. Part of the andalus-rag SPI. */
public record RagScoredChunk(RagChunk chunk, double score) {}
