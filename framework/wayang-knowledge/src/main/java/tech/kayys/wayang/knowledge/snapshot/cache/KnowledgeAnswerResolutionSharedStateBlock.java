package tech.kayys.wayang.knowledge.snapshot.cache;

import java.util.Arrays;
import java.util.Map;

/**
 * Represents a knowledge answer resolution shared state block.
 *
 * <p>Its components capture `id`, `data`, `size bytes`, `created at epoch millis`, `metadata`.</p>
 *
 * @param id the id
 * @param data the data
 * @param sizeBytes the size bytes
 * @param createdAtEpochMillis the created at epoch millis
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionSharedStateBlock(
        KnowledgeAnswerResolutionStateBlockId id,
        byte[] data,
        long sizeBytes,
        long createdAtEpochMillis,
        Map<String, String> metadata
) {
    public KnowledgeAnswerResolutionSharedStateBlock {
        data = data == null ? new byte[0] : Arrays.copyOf(data, data.length);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }

    @Override
    public byte[] data() {
        return Arrays.copyOf(data, data.length);
    }
}
