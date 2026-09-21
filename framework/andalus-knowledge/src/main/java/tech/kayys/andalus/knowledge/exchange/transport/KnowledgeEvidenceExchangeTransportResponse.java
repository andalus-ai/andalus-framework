package tech.kayys.andalus.knowledge.exchange.transport;

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
 * Represents a knowledge evidence exchange transport response.
 *
 * <p>Its components capture `request id`, `success`, `message`, `error code`, `error message`, and other values.</p>
 *
 * @param requestId the request id
 * @param success the success
 * @param message the message
 * @param errorCode the error code
 * @param errorMessage the error message
 * @param receivedAt the received at
 * @param completedAt the completed at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeTransportResponse(

        String requestId,

        boolean success,

        KnowledgeEvidenceExchangeTransportMessage message,

        String errorCode,

        String errorMessage,

        Instant receivedAt,

        Instant completedAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeTransportResponse {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public static KnowledgeEvidenceExchangeTransportResponse failure(
            String requestId,
            String errorCode,
            String errorMessage,
            Instant now
    ) {

        return new KnowledgeEvidenceExchangeTransportResponse(
                requestId,
                false,
                null,
                errorCode,
                errorMessage,
                now,
                now,
                Map.of()
        );
    }
}
