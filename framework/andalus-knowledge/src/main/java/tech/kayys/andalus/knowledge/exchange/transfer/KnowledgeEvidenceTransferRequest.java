package tech.kayys.andalus.knowledge.exchange.transfer;

import tech.kayys.andalus.knowledge.*;
import tech.kayys.andalus.knowledge.seal.*;
import tech.kayys.andalus.knowledge.snapshot.*;
import tech.kayys.andalus.knowledge.snapshot.pack.*;
import tech.kayys.andalus.knowledge.snapshot.artifact.*;
import tech.kayys.andalus.knowledge.snapshot.merkle.*;
import tech.kayys.andalus.knowledge.exchange.*;
import tech.kayys.andalus.knowledge.exchange.auth.*;
import tech.kayys.andalus.knowledge.exchange.session.*;
import tech.kayys.andalus.knowledge.exchange.binding.*;
import tech.kayys.andalus.knowledge.exchange.envelope.*;
import tech.kayys.andalus.knowledge.exchange.trust.*;
import tech.kayys.andalus.knowledge.exchange.identity.*;
import tech.kayys.andalus.knowledge.exchange.capability.*;
import tech.kayys.andalus.knowledge.exchange.protocol.*;
import tech.kayys.andalus.knowledge.exchange.transport.*;
import tech.kayys.andalus.knowledge.exchange.framing.*;
import tech.kayys.andalus.knowledge.exchange.transfer.*;
import tech.kayys.andalus.knowledge.exchange.replication.*;
import tech.kayys.andalus.knowledge.exchange.sync.*;
import tech.kayys.andalus.knowledge.exchange.federation.*;
import tech.kayys.andalus.knowledge.exchange.routing.*;
import tech.kayys.andalus.knowledge.exchange.fusion.*;


import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence transfer request.
 *
 * <p>Its components capture `transfer id`, `operation`, `session id`, `stream id`, `artifact id`, and other values.</p>
 *
 * @param transferId the transfer id
 * @param operation the operation
 * @param sessionId the session id
 * @param streamId the stream id
 * @param artifactId the artifact id
 * @param resourceId the resource id
 * @param offset the offset
 * @param length the length
 * @param expectedFingerprint the expected fingerprint
 * @param resumeTokenId the resume token id
 * @param requireMerkleProof the require merkle proof
 * @param requireFinalVerification the require final verification
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceTransferRequest(

        String transferId,

        KnowledgeEvidenceTransferOperation operation,

        String sessionId,

        String streamId,

        String artifactId,

        String resourceId,

        long offset,

        long length,

        String expectedFingerprint,

        String resumeTokenId,

        boolean requireMerkleProof,

        boolean requireFinalVerification,

        Instant issuedAt,

        Instant expiresAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceTransferRequest {

        if (offset < 0) {
            throw new IllegalArgumentException(
                    "offset must be >= 0"
            );
        }

        if (length < -1) {
            throw new IllegalArgumentException(
                    "length must be >= -1"
            );
        }

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean expiredAt(Instant now) {

        return expiresAt != null &&
                !now.isBefore(expiresAt);
    }

    public boolean rangeRequested() {
        return offset > 0 || length >= 0;
    }
}
