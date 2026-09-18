package tech.kayys.wayang.knowledge.snapshot.packing;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge answer resolution packed state block.
 *
 * <p>Its components capture `block id`, `algorithm`, `compression`, `data`, `uncompressed bytes`, and other values.</p>
 *
 * @param blockId the block id
 * @param algorithm the algorithm
 * @param compression the compression
 * @param data the data
 * @param uncompressedBytes the uncompressed bytes
 * @param compressedBytes the compressed bytes
 * @param logicalBlockIds the logical block ids
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionPackedStateBlock(
        String blockId,
        String algorithm,
        String compression,
        byte[] data,
        long uncompressedBytes,
        long compressedBytes,
        List<String> logicalBlockIds,
        Map<String, String> metadata
) {
    public KnowledgeAnswerResolutionPackedStateBlock {
        data = data == null ? new byte[0] : Arrays.copyOf(data, data.length);
        logicalBlockIds = logicalBlockIds == null ? List.of() : List.copyOf(logicalBlockIds);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    @Override
    public byte[] data() {
        return Arrays.copyOf(data, data.length);
    }
}
