package tech.kayys.wayang.knowledge.exchange.uncertainty;

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
