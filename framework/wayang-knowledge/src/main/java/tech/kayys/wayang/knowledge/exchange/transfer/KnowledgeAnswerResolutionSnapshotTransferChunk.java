package tech.kayys.wayang.knowledge.exchange.transfer;

import java.util.Arrays;

/**
 * Represents a knowledge answer resolution snapshot transfer chunk.
 *
 * <p>Its components capture `transfer id`, `snapshot id`, `offset`, `data`, `chunk fingerprint`, and other values.</p>
 *
 * @param transferId the transfer id
 * @param snapshotId the snapshot id
 * @param offset the offset
 * @param data the data
 * @param chunkFingerprint the chunk fingerprint
 * @param merkleProof the merkle proof
 */


public record KnowledgeAnswerResolutionSnapshotTransferChunk(
        String transferId,
        String snapshotId,
        long offset,
        byte[] data,
        String chunkFingerprint,
        String merkleProof
) {
    public KnowledgeAnswerResolutionSnapshotTransferChunk {
        data = data == null ? new byte[0] : Arrays.copyOf(data, data.length);
        if (offset < 0) {
            throw new IllegalArgumentException("offset must be >= 0");
        }
    }

    @Override
    public byte[] data() {
        return Arrays.copyOf(data, data.length);
    }
}
