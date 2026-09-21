package tech.kayys.andalus.knowledge.exchange.replication;

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
