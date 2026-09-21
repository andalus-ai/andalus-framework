package tech.kayys.andalus.knowledge.exchange.framing;

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
