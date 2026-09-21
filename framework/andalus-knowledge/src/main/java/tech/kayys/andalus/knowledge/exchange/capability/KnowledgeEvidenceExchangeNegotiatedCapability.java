package tech.kayys.andalus.knowledge.exchange.capability;

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


import java.util.Set;

/**
 * Represents a knowledge evidence exchange negotiated capability.
 *
 * <p>Its components capture `type`, `algorithms`, `formats`, `max artifact bytes`.</p>
 *
 * @param type the type
 * @param algorithms the algorithms
 * @param formats the formats
 * @param maxArtifactBytes the max artifact bytes
 */


public record KnowledgeEvidenceExchangeNegotiatedCapability(

        KnowledgeEvidenceExchangeCapabilityType type,

        Set<String> algorithms,

        Set<String> formats,

        long maxArtifactBytes

) {

    public KnowledgeEvidenceExchangeNegotiatedCapability {
        algorithms = algorithms == null
                ? Set.of()
                : Set.copyOf(algorithms);

        formats = formats == null
                ? Set.of()
                : Set.copyOf(formats);
    }
}
