package tech.kayys.wayang.knowledge.exchange.transfer;

import java.time.Instant;

/**
 * Represents a knowledge answer resolution snapshot transfer state.
 *
 * <p>Its components capture `transfer id`, `snapshot id`, `source runtime id`, `target runtime id`, `status`, and other values.</p>
 *
 * @param transferId the transfer id
 * @param snapshotId the snapshot id
 * @param sourceRuntimeId the source runtime id
 * @param targetRuntimeId the target runtime id
 * @param status the status
 * @param totalBytes the total bytes
 * @param bytesTransferred the bytes transferred
 * @param nextOffset the next offset
 * @param expectedFingerprint the expected fingerprint
 * @param merkleRoot the merkle root
 * @param createdAt the created at
 * @param updatedAt the updated at
 */


public record KnowledgeAnswerResolutionSnapshotTransferState(
        String transferId,
        String snapshotId,
        String sourceRuntimeId,
        String targetRuntimeId,
        KnowledgeAnswerResolutionSnapshotTransferStatus status,
        long totalBytes,
        long bytesTransferred,
        long nextOffset,
        String expectedFingerprint,
        String merkleRoot,
        Instant createdAt,
        Instant updatedAt
) {}
