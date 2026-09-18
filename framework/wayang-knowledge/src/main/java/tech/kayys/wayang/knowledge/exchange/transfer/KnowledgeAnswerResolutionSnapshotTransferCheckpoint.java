package tech.kayys.wayang.knowledge.exchange.transfer;

import java.time.Instant;

/**
 * Represents a knowledge answer resolution snapshot transfer checkpoint.
 *
 * <p>Its components capture `transfer id`, `snapshot id`, `next offset`, `bytes transferred`, `partial fingerprint`, and other values.</p>
 *
 * @param transferId the transfer id
 * @param snapshotId the snapshot id
 * @param nextOffset the next offset
 * @param bytesTransferred the bytes transferred
 * @param partialFingerprint the partial fingerprint
 * @param updatedAt the updated at
 */


public record KnowledgeAnswerResolutionSnapshotTransferCheckpoint(
        String transferId,
        String snapshotId,
        long nextOffset,
        long bytesTransferred,
        String partialFingerprint,
        Instant updatedAt
) {}
