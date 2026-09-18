package tech.kayys.wayang.knowledge.exchange.replication;

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


import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence artifact replication request.
 *
 * <p>Its components capture `replication id`, `artifact id`, `source runtime id`, `target runtime ids`, `tenant id`, and other values.</p>
 *
 * @param replicationId the replication id
 * @param artifactId the artifact id
 * @param sourceRuntimeId the source runtime id
 * @param targetRuntimeIds the target runtime ids
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param desiredReplicas the desired replicas
 * @param requireVerification the require verification
 * @param requireMerkleProof the require merkle proof
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactReplicationRequest(

        String replicationId,

        String artifactId,

        String sourceRuntimeId,

        List<String> targetRuntimeIds,

        String tenantId,

        String workspaceId,

        String projectId,

        int desiredReplicas,

        boolean requireVerification,

        boolean requireMerkleProof,

        Instant issuedAt,

        Instant expiresAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceArtifactReplicationRequest {

        targetRuntimeIds =
                targetRuntimeIds == null
                        ? List.of()
                        : List.copyOf(targetRuntimeIds);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean expiredAt(
            Instant now
    ) {

        return expiresAt != null &&
                !now.isBefore(expiresAt);
    }
}
