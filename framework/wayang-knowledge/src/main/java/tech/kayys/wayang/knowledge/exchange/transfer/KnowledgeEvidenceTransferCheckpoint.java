package tech.kayys.wayang.knowledge.exchange.transfer;

import tech.kayys.wayang.knowledge.*;
import tech.kayys.wayang.knowledge.seal.*;
import tech.kayys.wayang.knowledge.snapshot.*;
import tech.kayys.wayang.knowledge.snapshot.pack.*;
import tech.kayys.wayang.knowledge.snapshot.artifact.*;
import tech.kayys.wayang.knowledge.snapshot.merkle.*;
import tech.kayys.wayang.knowledge.exchange.*;
import tech.kayys.wayang.knowledge.exchange.auth.*;
import tech.kayys.wayang.knowledge.exchange.session.*;
import tech.kayys.wayang.knowledge.exchange.binding.*;
import tech.kayys.wayang.knowledge.exchange.envelope.*;
import tech.kayys.wayang.knowledge.exchange.trust.*;
import tech.kayys.wayang.knowledge.exchange.identity.*;
import tech.kayys.wayang.knowledge.exchange.capability.*;
import tech.kayys.wayang.knowledge.exchange.protocol.*;
import tech.kayys.wayang.knowledge.exchange.transport.*;
import tech.kayys.wayang.knowledge.exchange.framing.*;
import tech.kayys.wayang.knowledge.exchange.transfer.*;
import tech.kayys.wayang.knowledge.exchange.replication.*;
import tech.kayys.wayang.knowledge.exchange.sync.*;
import tech.kayys.wayang.knowledge.exchange.federation.*;
import tech.kayys.wayang.knowledge.exchange.routing.*;
import tech.kayys.wayang.knowledge.exchange.fusion.*;


import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence transfer checkpoint.
 *
 * <p>Its components capture `transfer id`, `session id`, `stream id`, `artifact id`, `resource id`, and other values.</p>
 *
 * @param transferId the transfer id
 * @param sessionId the session id
 * @param streamId the stream id
 * @param artifactId the artifact id
 * @param resourceId the resource id
 * @param offset the offset
 * @param nextSequence the next sequence
 * @param artifactFingerprint the artifact fingerprint
 * @param resourceFingerprint the resource fingerprint
 * @param merkleRoot the merkle root
 * @param createdAt the created at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceTransferCheckpoint(

        String transferId,

        String sessionId,

        String streamId,

        String artifactId,

        String resourceId,

        long offset,

        long nextSequence,

        String artifactFingerprint,

        String resourceFingerprint,

        String merkleRoot,

        Instant createdAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceTransferCheckpoint {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
