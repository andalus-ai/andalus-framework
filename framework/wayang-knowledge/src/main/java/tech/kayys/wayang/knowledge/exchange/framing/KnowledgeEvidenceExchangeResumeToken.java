package tech.kayys.wayang.knowledge.exchange.framing;

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


import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence exchange resume token.
 *
 * <p>Its components capture `token id`, `session id`, `stream id`, `artifact id`, `resource id`, and other values.</p>
 *
 * @param tokenId the token id
 * @param sessionId the session id
 * @param streamId the stream id
 * @param artifactId the artifact id
 * @param resourceId the resource id
 * @param offset the offset
 * @param nextSequence the next sequence
 * @param resourceFingerprint the resource fingerprint
 * @param createdAt the created at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeResumeToken(

        String tokenId,

        String sessionId,

        String streamId,

        String artifactId,

        String resourceId,

        long offset,

        long nextSequence,

        String resourceFingerprint,

        Instant createdAt,

        Instant expiresAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeResumeToken {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant now) {

        return expiresAt == null ||
                now.isBefore(expiresAt);
    }
}
