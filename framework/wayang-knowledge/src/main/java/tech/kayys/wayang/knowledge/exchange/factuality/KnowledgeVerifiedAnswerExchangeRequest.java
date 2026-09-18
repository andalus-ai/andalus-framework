package tech.kayys.wayang.knowledge.exchange.factuality;

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
import java.util.Map;

/**
 * Represents a knowledge verified answer exchange request.
 *
 * <p>Its components capture `request id`, `operation`, `artifact id`, `response id`, `snapshot id`, and other values.</p>
 *
 * @param requestId the request id
 * @param operation the operation
 * @param artifactId the artifact id
 * @param responseId the response id
 * @param snapshotId the snapshot id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param requestingRuntimeId the requesting runtime id
 * @param requireProvenance the require provenance
 * @param requireSnapshot the require snapshot
 * @param requireIntegrity the require integrity
 * @param requireSeal the require seal
 * @param allowRemoteFetch the allow remote fetch
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeVerifiedAnswerExchangeRequest(

        String requestId,

        KnowledgeVerifiedAnswerExchangeOperation operation,

        String artifactId,

        String responseId,

        String snapshotId,

        String tenantId,

        String workspaceId,

        String projectId,

        String requestingRuntimeId,

        boolean requireProvenance,

        boolean requireSnapshot,

        boolean requireIntegrity,

        boolean requireSeal,

        boolean allowRemoteFetch,

        Instant issuedAt,

        Instant expiresAt,

        Map<String, String> metadata
) {

    public KnowledgeVerifiedAnswerExchangeRequest {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean expiredAt(Instant now) {

        return expiresAt != null
                && now != null
                && now.isAfter(expiresAt);
    }
}
