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
import java.util.Objects;
import java.util.Set;

/**
 * Represents a knowledge evidence exchange protocol hello.
 *
 * <p>Its components capture `message id`, `runtime id`, `protocol version`, `supported protocol versions`, `correlation id`, and other values.</p>
 *
 * @param messageId the message id
 * @param runtimeId the runtime id
 * @param protocolVersion the protocol version
 * @param supportedProtocolVersions the supported protocol versions
 * @param correlationId the correlation id
 * @param nonce the nonce
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeProtocolHello(

        String messageId,

        String runtimeId,

        KnowledgeEvidenceExchangeProtocolVersion protocolVersion,

        Set<KnowledgeEvidenceExchangeProtocolVersion>
                supportedProtocolVersions,

        String correlationId,

        String nonce,

        Instant issuedAt,

        Instant expiresAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeProtocolHello {
        Objects.requireNonNull(messageId);
        Objects.requireNonNull(runtimeId);
        Objects.requireNonNull(protocolVersion);
        Objects.requireNonNull(nonce);

        supportedProtocolVersions =
                supportedProtocolVersions == null
                        ? Set.of(protocolVersion)
                        : Set.copyOf(supportedProtocolVersions);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant at) {

        if (issuedAt != null &&
                at.isBefore(issuedAt)) {
            return false;
        }

        return expiresAt == null ||
                at.isBefore(expiresAt);
    }
}
