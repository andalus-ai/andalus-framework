package tech.kayys.wayang.knowledge.exchange.transfer;

/**
 * Represents a knowledge answer resolution snapshot transfer request.
 *
 * <p>Its components capture `transfer id`, `snapshot id`, `source runtime id`, `target runtime id`, `tenant id`, and other values.</p>
 *
 * @param transferId the transfer id
 * @param snapshotId the snapshot id
 * @param sourceRuntimeId the source runtime id
 * @param targetRuntimeId the target runtime id
 * @param tenantId the tenant id
 * @param epochId the epoch id
 * @param offset the offset
 * @param length the length
 * @param requireMerkleProof the require merkle proof
 * @param expectedFingerprint the expected fingerprint
 */


public record KnowledgeAnswerResolutionSnapshotTransferRequest(
        String transferId,
        String snapshotId,
        String sourceRuntimeId,
        String targetRuntimeId,
        String tenantId,
        String epochId,
        long offset,
        long length,
        boolean requireMerkleProof,
        String expectedFingerprint
) {
    public KnowledgeAnswerResolutionSnapshotTransferRequest {
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0");
        }
        if (length <= 0) {
            throw new IllegalArgumentException("length must be > 0");
        }
    }
}
