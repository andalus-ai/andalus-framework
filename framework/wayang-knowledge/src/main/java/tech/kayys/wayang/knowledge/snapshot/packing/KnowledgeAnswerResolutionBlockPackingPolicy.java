package tech.kayys.wayang.knowledge.snapshot.packing;

/**
 * Represents a knowledge answer resolution block packing policy.
 *
 * <p>Its components capture `target block bytes`, `minimum block bytes`, `maximum block bytes`, `maximum pack entries`, `enable packing`, and other values.</p>
 *
 * @param targetBlockBytes the target block bytes
 * @param minimumBlockBytes the minimum block bytes
 * @param maximumBlockBytes the maximum block bytes
 * @param maximumPackEntries the maximum pack entries
 * @param enablePacking the enable packing
 * @param enableSplitting the enable splitting
 * @param enableCompression the enable compression
 * @param minimumCompressionSavingsRatio the minimum compression savings ratio
 */


public record KnowledgeAnswerResolutionBlockPackingPolicy(
        long targetBlockBytes,
        long minimumBlockBytes,
        long maximumBlockBytes,
        long maximumPackEntries,
        boolean enablePacking,
        boolean enableSplitting,
        boolean enableCompression,
        double minimumCompressionSavingsRatio
) {
    public static KnowledgeAnswerResolutionBlockPackingPolicy defaults() {
        return new KnowledgeAnswerResolutionBlockPackingPolicy(
                4 * 1024 * 1024,
                256 * 1024,
                8 * 1024 * 1024,
                4096,
                true,
                true,
                true,
                0.10
        );
    }
}
