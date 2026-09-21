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


import java.util.Map;

/**
 * Represents a knowledge evidence exchange runtime handshake result.
 *
 * <p>Its components capture `status`, `handshake`, `reason`, `metadata`.</p>
 *
 * @param status the status
 * @param handshake the handshake
 * @param reason the reason
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeRuntimeHandshakeResult(

        KnowledgeEvidenceExchangeRuntimeHandshakeStatus status,

        KnowledgeEvidenceExchangeRuntimeHandshake handshake,

        String reason,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeRuntimeHandshakeResult {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean successful() {
        return status ==
                KnowledgeEvidenceExchangeRuntimeHandshakeStatus
                        .MUTUALLY_TRUSTED;
    }
}
