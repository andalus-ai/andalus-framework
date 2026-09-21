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


import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence exchange capability negotiation request.
 *
 * <p>Its components capture `local runtime id`, `remote runtime id`, `local manifest`, `remote manifest`, `requested at`, and other values.</p>
 *
 * @param localRuntimeId the local runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param localManifest the local manifest
 * @param remoteManifest the remote manifest
 * @param requestedAt the requested at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeCapabilityNegotiationRequest(

        String localRuntimeId,

        String remoteRuntimeId,

        KnowledgeEvidenceExchangeCapabilityManifest
                localManifest,

        KnowledgeEvidenceExchangeCapabilityManifest
                remoteManifest,

        Instant requestedAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeCapabilityNegotiationRequest {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
