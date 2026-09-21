package tech.kayys.andalus.knowledge.exchange.attestation;

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
import tech.kayys.andalus.knowledge.exchange.transfer.*;
import tech.kayys.andalus.knowledge.exchange.replication.*;
import tech.kayys.andalus.knowledge.exchange.sync.*;
import tech.kayys.andalus.knowledge.exchange.federation.*;
import tech.kayys.andalus.knowledge.exchange.routing.*;
import tech.kayys.andalus.knowledge.exchange.fusion.*;
import tech.kayys.andalus.knowledge.exchange.coverage.*;
import tech.kayys.andalus.knowledge.exchange.gap.*;
import tech.kayys.andalus.knowledge.exchange.attribution.*;
import tech.kayys.andalus.knowledge.exchange.contradiction.*;
import tech.kayys.andalus.knowledge.exchange.factuality.*;
import tech.kayys.andalus.knowledge.exchange.uncertainty.*;
import tech.kayys.andalus.knowledge.exchange.compact.*;
import tech.kayys.andalus.knowledge.exchange.resolution.*;
import tech.kayys.andalus.knowledge.exchange.quorum.*;
import tech.kayys.andalus.knowledge.exchange.selection.*;
import tech.kayys.andalus.knowledge.exchange.coordination.*;
import tech.kayys.andalus.knowledge.exchange.attestation.*;
import tech.kayys.andalus.knowledge.exchange.proof.*;
import tech.kayys.andalus.knowledge.exchange.validity.*;
import tech.kayys.andalus.knowledge.exchange.lease.*;
import tech.kayys.andalus.knowledge.exchange.recovery.*;


import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge answer resolution consensus certificate.
 *
 * <p>Its components capture `certificate id`, `consensus id`, `key fingerprint`, `winning resolution fingerprint`, `dependency fingerprint`, and other values.</p>
 *
 * @param certificateId the certificate id
 * @param consensusId the consensus id
 * @param keyFingerprint the key fingerprint
 * @param winningResolutionFingerprint the winning resolution fingerprint
 * @param dependencyFingerprint the dependency fingerprint
 * @param participantRuntimeIds the participant runtime ids
 * @param agreeingRuntimeIds the agreeing runtime ids
 * @param quorum the quorum
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param certificateFingerprint the certificate fingerprint
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionConsensusCertificate(

        String certificateId,

        String consensusId,

        String keyFingerprint,

        String winningResolutionFingerprint,

        String dependencyFingerprint,

        List<String> participantRuntimeIds,

        List<String> agreeingRuntimeIds,

        int quorum,

        Instant issuedAt,

        Instant expiresAt,

        String certificateFingerprint,

        Map<String, String> metadata
) {

    public KnowledgeAnswerResolutionConsensusCertificate {

        participantRuntimeIds =
                participantRuntimeIds == null
                        ? List.of()
                        : List.copyOf(participantRuntimeIds);

        agreeingRuntimeIds =
                agreeingRuntimeIds == null
                        ? List.of()
                        : List.copyOf(agreeingRuntimeIds);

        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }
}
