package tech.kayys.wayang.knowledge.exchange.transfer;

import java.time.Instant;

/**
 * Represents a knowledge answer resolution snapshot descriptor.
 *
 * <p>Its components capture `snapshot id`, `runtime id`, `tenant id`, `epoch id`, `last applied index`, and other values.</p>
 *
 * @param snapshotId the snapshot id
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param epochId the epoch id
 * @param lastAppliedIndex the last applied index
 * @param term the term
 * @param totalBytes the total bytes
 * @param chunkSize the chunk size
 * @param stateFingerprint the state fingerprint
 * @param merkleRoot the merkle root
 * @param createdAt the created at
 * @param expiresAt the expires at
 */


public record KnowledgeAnswerResolutionSnapshotDescriptor(
        String snapshotId,
        String runtimeId,
        String tenantId,
        String epochId,
        long lastAppliedIndex,
        long term,
        long totalBytes,
        int chunkSize,
        String stateFingerprint,
        String merkleRoot,
        Instant createdAt,
        Instant expiresAt
) {}
