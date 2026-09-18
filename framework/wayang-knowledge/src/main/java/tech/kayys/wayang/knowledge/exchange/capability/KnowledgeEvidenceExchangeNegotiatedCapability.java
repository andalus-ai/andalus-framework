package tech.kayys.wayang.knowledge.exchange.capability;

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
