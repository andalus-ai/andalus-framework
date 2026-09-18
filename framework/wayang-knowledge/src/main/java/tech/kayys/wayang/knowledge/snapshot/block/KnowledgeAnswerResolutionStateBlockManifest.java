package tech.kayys.wayang.knowledge.snapshot.block;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge answer resolution state block manifest.
 *
 * <p>Its components capture `snapshot id`, `tenant id`, `epoch id`, `last applied index`, `state fingerprint`, and other values.</p>
 *
 * @param snapshotId the snapshot id
 * @param tenantId the tenant id
 * @param epochId the epoch id
 * @param lastAppliedIndex the last applied index
 * @param stateFingerprint the state fingerprint
 * @param merkleRoot the merkle root
 * @param blockIds the block ids
 * @param totalBytes the total bytes
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionStateBlockManifest(
        String snapshotId,
        String tenantId,
        String epochId,
        long lastAppliedIndex,
        String stateFingerprint,
        String merkleRoot,
        List<String> blockIds,
        long totalBytes,
        Map<String, String> metadata
) {
    public KnowledgeAnswerResolutionStateBlockManifest {
        blockIds = blockIds == null ? List.of() : List.copyOf(blockIds);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
