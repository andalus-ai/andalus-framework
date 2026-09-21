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


import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a knowledge evidence exchange runtime capability.
 *
 * <p>Its components capture `type`, `supported`, `algorithms`, `formats`, `max artifact bytes`, and other values.</p>
 *
 * @param type the type
 * @param supported the supported
 * @param algorithms the algorithms
 * @param formats the formats
 * @param maxArtifactBytes the max artifact bytes
 * @param required the required
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeRuntimeCapability(

        KnowledgeEvidenceExchangeCapabilityType type,

        boolean supported,

        Set<String> algorithms,

        Set<String> formats,

        long maxArtifactBytes,

        boolean required,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeRuntimeCapability {
        Objects.requireNonNull(type);

        algorithms = algorithms == null
                ? Set.of()
                : Set.copyOf(algorithms);

        formats = formats == null
                ? Set.of()
                : Set.copyOf(formats);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
