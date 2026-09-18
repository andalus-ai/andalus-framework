package tech.kayys.wayang.knowledge.exchange.proof;

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
import tech.kayys.wayang.knowledge.exchange.transfer.*;
import tech.kayys.wayang.knowledge.exchange.replication.*;
import tech.kayys.wayang.knowledge.exchange.sync.*;
import tech.kayys.wayang.knowledge.exchange.federation.*;
import tech.kayys.wayang.knowledge.exchange.routing.*;
import tech.kayys.wayang.knowledge.exchange.fusion.*;
import tech.kayys.wayang.knowledge.exchange.coverage.*;
import tech.kayys.wayang.knowledge.exchange.gap.*;
import tech.kayys.wayang.knowledge.exchange.attribution.*;
import tech.kayys.wayang.knowledge.exchange.contradiction.*;
import tech.kayys.wayang.knowledge.exchange.factuality.*;
import tech.kayys.wayang.knowledge.exchange.uncertainty.*;
import tech.kayys.wayang.knowledge.exchange.compact.*;
import tech.kayys.wayang.knowledge.exchange.resolution.*;
import tech.kayys.wayang.knowledge.exchange.quorum.*;
import tech.kayys.wayang.knowledge.exchange.selection.*;
import tech.kayys.wayang.knowledge.exchange.coordination.*;
import tech.kayys.wayang.knowledge.exchange.attestation.*;
import tech.kayys.wayang.knowledge.exchange.proof.*;
import tech.kayys.wayang.knowledge.exchange.validity.*;
import tech.kayys.wayang.knowledge.exchange.lease.*;
import tech.kayys.wayang.knowledge.exchange.recovery.*;


import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge answer resolution consensus attestation.
 *
 * <p>Its components capture `attestation certificate id`, `consensus id`, `key fingerprint`, `resolution fingerprint`, `dependency fingerprint`, and other values.</p>
 *
 * @param attestationCertificateId the attestation certificate id
 * @param consensusId the consensus id
 * @param keyFingerprint the key fingerprint
 * @param resolutionFingerprint the resolution fingerprint
 * @param dependencyFingerprint the dependency fingerprint
 * @param participantSetFingerprint the participant set fingerprint
 * @param quorum the quorum
 * @param participantRuntimeIds the participant runtime ids
 * @param attestations the attestations
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param status the status
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionConsensusAttestation(

        String attestationCertificateId,

        String consensusId,

        String keyFingerprint,

        String resolutionFingerprint,

        String dependencyFingerprint,

        String participantSetFingerprint,

        int quorum,

        List<String> participantRuntimeIds,

        List<KnowledgeAnswerResolutionAttestation>
                attestations,

        Instant issuedAt,

        Instant expiresAt,

        KnowledgeAnswerResolutionAttestationStatus status,

        Map<String, String> metadata
) {

    public KnowledgeAnswerResolutionConsensusAttestation {

        participantRuntimeIds =
                participantRuntimeIds == null
                        ? List.of()
                        : List.copyOf(
                                participantRuntimeIds
                        );

        attestations =
                attestations == null
                        ? List.of()
                        : List.copyOf(
                                attestations
                        );

        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }
}
