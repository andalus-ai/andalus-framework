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


import java.util.Map;
import java.util.Set;

/**
 * Represents a knowledge evidence exchange transport descriptor.
 *
 * <p>Its components capture `transport id`, `type`, `capabilities`, `protocol version`, `max concurrent streams`, and other values.</p>
 *
 * @param transportId the transport id
 * @param type the type
 * @param capabilities the capabilities
 * @param protocolVersion the protocol version
 * @param maxConcurrentStreams the max concurrent streams
 * @param maxFrameBytes the max frame bytes
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeTransportDescriptor(

        String transportId,

        KnowledgeEvidenceExchangeTransportType type,

        Set<KnowledgeEvidenceExchangeTransportCapability>
                capabilities,

        String protocolVersion,

        int maxConcurrentStreams,

        long maxFrameBytes,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeTransportDescriptor {
        capabilities = capabilities == null
                ? Set.of()
                : Set.copyOf(capabilities);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean supports(
            KnowledgeEvidenceExchangeTransportCapability capability
    ) {
        return capabilities.contains(capability);
    }
}
