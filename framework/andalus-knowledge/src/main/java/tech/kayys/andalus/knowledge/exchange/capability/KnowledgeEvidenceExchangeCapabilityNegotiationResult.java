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


import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence exchange capability negotiation result.
 *
 * <p>Its components capture `status`, `protocol version`, `capabilities`, `local manifest fingerprint`, `remote manifest fingerprint`, and other values.</p>
 *
 * @param status the status
 * @param protocolVersion the protocol version
 * @param capabilities the capabilities
 * @param localManifestFingerprint the local manifest fingerprint
 * @param remoteManifestFingerprint the remote manifest fingerprint
 * @param diagnostics the diagnostics
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeCapabilityNegotiationResult(

        KnowledgeEvidenceExchangeCapabilityNegotiationStatus status,

        KnowledgeEvidenceExchangeProtocolVersion protocolVersion,

        List<KnowledgeEvidenceExchangeNegotiatedCapability>
                capabilities,

        String localManifestFingerprint,

        String remoteManifestFingerprint,

        List<String> diagnostics,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeCapabilityNegotiationResult {
        capabilities = capabilities == null
                ? List.of()
                : List.copyOf(capabilities);

        diagnostics = diagnostics == null
                ? List.of()
                : List.copyOf(diagnostics);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean successful() {

        return status ==
                KnowledgeEvidenceExchangeCapabilityNegotiationStatus
                        .NEGOTIATED;
    }
}
