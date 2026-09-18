package tech.kayys.wayang.knowledge.snapshot.packing;

import java.util.Arrays;
import java.util.Map;

/**
 * Represents a knowledge answer resolution logical state block.
 *
 * <p>Its components capture `logical id`, `data`, `metadata`.</p>
 *
 * @param logicalId the logical id
 * @param data the data
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionLogicalStateBlock(
        String logicalId,
        byte[] data,
        Map<String, String> metadata
) {
    public KnowledgeAnswerResolutionLogicalStateBlock {
        data = data == null ? new byte[0] : Arrays.copyOf(data, data.length);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    public long sizeBytes() {
        return data == null ? 0 : data.length;
    }

    @Override
    public byte[] data() {
        return Arrays.copyOf(data, data.length);
    }
}
