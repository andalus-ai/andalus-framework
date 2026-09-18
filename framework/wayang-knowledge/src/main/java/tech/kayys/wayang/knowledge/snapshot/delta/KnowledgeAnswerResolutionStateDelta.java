package tech.kayys.wayang.knowledge.snapshot.delta;

import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge answer resolution state delta.
 *
 * <p>Its components capture `delta id`, `source snapshot id`, `target snapshot id`, `tenant id`, `source epoch id`, and other values.</p>
 *
 * @param deltaId the delta id
 * @param sourceSnapshotId the source snapshot id
 * @param targetSnapshotId the target snapshot id
 * @param tenantId the tenant id
 * @param sourceEpochId the source epoch id
 * @param targetEpochId the target epoch id
 * @param sourceIndex the source index
 * @param targetIndex the target index
 * @param sourceStateFingerprint the source state fingerprint
 * @param targetStateFingerprint the target state fingerprint
 * @param deltaFingerprint the delta fingerprint
 * @param operations the operations
 * @param estimatedFullSnapshotBytes the estimated full snapshot bytes
 * @param estimatedDeltaBytes the estimated delta bytes
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionStateDelta(
        String deltaId,
        String sourceSnapshotId,
        String targetSnapshotId,
        String tenantId,
        String sourceEpochId,
        String targetEpochId,
        long sourceIndex,
        long targetIndex,
        String sourceStateFingerprint,
        String targetStateFingerprint,
        String deltaFingerprint,
        List<KnowledgeAnswerResolutionStateDeltaOperation> operations,
        long estimatedFullSnapshotBytes,
        long estimatedDeltaBytes,
        Map<String, String> metadata
) {
    public KnowledgeAnswerResolutionStateDelta {
        operations = operations == null ? List.of() : List.copyOf(operations);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
