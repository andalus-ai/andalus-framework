package tech.kayys.wayang.knowledge.exchange.protocol;

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
 * Represents a knowledge evidence exchange protocol close message.
 *
 * <p>Its components capture `message id`, `correlation id`, `session id`, `reason`, `issued at`, and other values.</p>
 *
 * @param messageId the message id
 * @param correlationId the correlation id
 * @param sessionId the session id
 * @param reason the reason
 * @param issuedAt the issued at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeProtocolCloseMessage(

        String messageId,

        String correlationId,

        String sessionId,

        String reason,

        Instant issuedAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeProtocolCloseMessage {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
