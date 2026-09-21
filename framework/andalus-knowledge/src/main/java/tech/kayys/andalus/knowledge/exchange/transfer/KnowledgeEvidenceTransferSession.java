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
 * Represents a knowledge evidence transfer session.
 *
 * <p>Its components capture `transfer id`, `operation`, `session id`, `stream id`, `artifact id`, and other values.</p>
 *
 * @param transferId the transfer id
 * @param operation the operation
 * @param sessionId the session id
 * @param streamId the stream id
 * @param artifactId the artifact id
 * @param resourceId the resource id
 * @param startOffset the start offset
 * @param currentOffset the current offset
 * @param totalLength the total length
 * @param nextSequence the next sequence
 * @param state the state
 * @param artifactFingerprint the artifact fingerprint
 * @param resourceFingerprint the resource fingerprint
 * @param merkleRoot the merkle root
 * @param createdAt the created at
 * @param updatedAt the updated at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceTransferSession(

        String transferId,

        KnowledgeEvidenceTransferOperation operation,

        String sessionId,

        String streamId,

        String artifactId,

        String resourceId,

        long startOffset,

        long currentOffset,

        long totalLength,

        long nextSequence,

        KnowledgeEvidenceTransferState state,

        String artifactFingerprint,

        String resourceFingerprint,

        String merkleRoot,

        Instant createdAt,

        Instant updatedAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceTransferSession {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean terminal() {

        return switch (state) {

            case COMPLETED,
                 CANCELLED,
                 FAILED,
                 EXPIRED -> true;

            default -> false;
        };
    }
}
