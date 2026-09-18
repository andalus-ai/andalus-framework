package tech.kayys.wayang.knowledge.snapshot.cache;

/**
 * Represents a knowledge answer resolution snapshot block reference.
 *
 * <p>Its components capture `snapshot id`, `tenant id`, `workspace id`, `project id`, `block id`, and other values.</p>
 *
 * @param snapshotId the snapshot id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param blockId the block id
 * @param ordinal the ordinal
 * @param createdAtEpochMillis the created at epoch millis
 */


public record KnowledgeAnswerResolutionSnapshotBlockReference(
        String snapshotId,
        String tenantId,
        String workspaceId,
        String projectId,
        KnowledgeAnswerResolutionStateBlockId blockId,
        long ordinal,
        long createdAtEpochMillis
) {}
