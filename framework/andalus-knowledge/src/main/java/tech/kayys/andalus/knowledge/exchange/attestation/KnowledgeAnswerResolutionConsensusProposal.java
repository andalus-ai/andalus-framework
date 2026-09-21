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
 * Represents a knowledge answer resolution consensus proposal.
 *
 * <p>Its components capture `consensus id`, `key fingerprint`, `tenant id`, `workspace id`, `project id`, and other values.</p>
 *
 * @param consensusId the consensus id
 * @param keyFingerprint the key fingerprint
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param proposerRuntimeId the proposer runtime id
 * @param epochId the epoch id
 * @param epochSequence the epoch sequence
 * @param participantSetFingerprint the participant set fingerprint
 * @param participantRuntimeIds the participant runtime ids
 * @param proposedResolutionFingerprint the proposed resolution fingerprint
 * @param proposedDependencyFingerprint the proposed dependency fingerprint
 * @param versionVector the version vector
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionConsensusProposal(
        String consensusId,
        String keyFingerprint,
        String tenantId,
        String workspaceId,
        String projectId,
        String proposerRuntimeId,
        String epochId,
        long epochSequence,
        String participantSetFingerprint,
        List<String> participantRuntimeIds,
        String proposedResolutionFingerprint,
        String proposedDependencyFingerprint,
        KnowledgeAnswerResolutionVersionVector versionVector,
        Instant issuedAt,
        Instant expiresAt,
        Map<String, String> metadata
) {
    public KnowledgeAnswerResolutionConsensusProposal(
            String consensusId,
            String keyFingerprint,
            String tenantId,
            String workspaceId,
            String projectId,
            String proposerRuntimeId,
            List<String> participantRuntimeIds,
            String proposedResolutionFingerprint,
            String proposedDependencyFingerprint,
            KnowledgeAnswerResolutionVersionVector versionVector,
            Instant issuedAt,
            Instant expiresAt,
            Map<String, String> metadata) {
        this(consensusId, keyFingerprint, tenantId, workspaceId, projectId,
                proposerRuntimeId, null, 0L, null, participantRuntimeIds,
                proposedResolutionFingerprint, proposedDependencyFingerprint,
                versionVector, issuedAt, expiresAt, metadata);
    }

    public KnowledgeAnswerResolutionConsensusProposal {
        participantRuntimeIds = participantRuntimeIds == null
                ? List.of()
                : List.copyOf(participantRuntimeIds);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
