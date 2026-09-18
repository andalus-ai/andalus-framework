package tech.kayys.wayang.knowledge.snapshot.packing;

/**
 * Represents a knowledge answer resolution compression decision.
 *
 * <p>Its components capture `use compression`, `algorithm`, `original bytes`, `compressed bytes`, `savings ratio`, and other values.</p>
 *
 * @param useCompression the use compression
 * @param algorithm the algorithm
 * @param originalBytes the original bytes
 * @param compressedBytes the compressed bytes
 * @param savingsRatio the savings ratio
 * @param reason the reason
 */


public record KnowledgeAnswerResolutionCompressionDecision(
        boolean useCompression,
        String algorithm,
        long originalBytes,
        long compressedBytes,
        double savingsRatio,
        String reason
) {}
