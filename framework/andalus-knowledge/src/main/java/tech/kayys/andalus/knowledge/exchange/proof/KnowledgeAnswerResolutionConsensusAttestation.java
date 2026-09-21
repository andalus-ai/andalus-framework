package tech.kayys.andalus.knowledge.exchange.proof;

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
