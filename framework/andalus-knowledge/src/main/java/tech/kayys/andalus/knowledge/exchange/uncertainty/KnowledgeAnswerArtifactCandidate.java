package tech.kayys.andalus.knowledge.exchange.uncertainty;

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


import java.util.Map;

/**
 * Represents a knowledge answer artifact candidate.
 *
 * <p>Its components capture `artifact id`, `response id`, `runtime id`, `tenant id`, `workspace id`, and other values.</p>
 *
 * @param artifactId the artifact id
 * @param responseId the response id
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param agentId the agent id
 * @param semanticScore the semantic score
 * @param authorityScore the authority score
 * @param trustScore the trust score
 * @param freshnessScore the freshness score
 * @param verificationScore the verification score
 * @param finalScore the final score
 * @param verified the verified
 * @param sealed the sealed
 * @param local the local
 * @param metadata the metadata
 */


public record KnowledgeAnswerArtifactCandidate(

        String artifactId,

        String responseId,

        String runtimeId,

        String tenantId,

        String workspaceId,

        String projectId,

        String agentId,

        double semanticScore,

        double authorityScore,

        double trustScore,

        double freshnessScore,

        double verificationScore,

        double finalScore,

        boolean verified,

        boolean sealed,

        boolean local,

        Map<String, String> metadata
) {

    public KnowledgeAnswerArtifactCandidate {
        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }
}
