package tech.kayys.wayang.knowledge.snapshot.block;

import java.util.Arrays;
import java.util.Map;

/**
 * Represents a knowledge answer resolution state block.
 *
 * <p>Its components capture `block id`, `algorithm`, `fingerprint`, `data`, `size bytes`, and other values.</p>
 *
 * @param blockId the block id
 * @param algorithm the algorithm
 * @param fingerprint the fingerprint
 * @param data the data
 * @param sizeBytes the size bytes
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionStateBlock(
        String blockId,
        String algorithm,
        String fingerprint,
        byte[] data,
        long sizeBytes,
        Map<String, String> metadata
) {
    public KnowledgeAnswerResolutionStateBlock {
        data = data == null ? new byte[0] : Arrays.copyOf(data, data.length);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    @Override
    public byte[] data() {
        return Arrays.copyOf(data, data.length);
    }
}
