package tech.kayys.andalus.knowledge.exchange.identity;

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
 * Represents a knowledge evidence exchange runtime enrollment request.
 *
 * <p>Its components capture `identity`, `requested by`, `requested at`, `reason`, `metadata`.</p>
 *
 * @param identity the identity
 * @param requestedBy the requested by
 * @param requestedAt the requested at
 * @param reason the reason
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeRuntimeEnrollmentRequest(

        KnowledgeEvidenceExchangeRuntimeIdentity identity,

        String requestedBy,

        Instant requestedAt,

        String reason,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeRuntimeEnrollmentRequest {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
