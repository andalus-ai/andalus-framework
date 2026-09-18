package tech.kayys.wayang.knowledge.exchange.transport;

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


import java.time.Duration;
import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence exchange transport request.
 *
 * <p>Its components capture `request id`, `session id`, `message`, `issued at`, `expires at`, and other values.</p>
 *
 * @param requestId the request id
 * @param sessionId the session id
 * @param message the message
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param deadline the deadline
 * @param streaming the streaming
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeTransportRequest(

        String requestId,

        String sessionId,

        KnowledgeEvidenceExchangeTransportMessage message,

        Instant issuedAt,

        Instant expiresAt,

        Duration deadline,

        boolean streaming,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeTransportRequest {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean expiredAt(Instant now) {

        return expiresAt != null &&
                !now.isBefore(expiresAt);
    }
}
