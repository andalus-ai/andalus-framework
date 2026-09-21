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
import java.util.Map;

/**
 * Represents a knowledge evidence artifact replication job.
 *
 * <p>Its components capture `replication id`, `artifact id`, `source runtime id`, `desired replicas`, `state`, and other values.</p>
 *
 * @param replicationId the replication id
 * @param artifactId the artifact id
 * @param sourceRuntimeId the source runtime id
 * @param desiredReplicas the desired replicas
 * @param state the state
 * @param successfulReplicas the successful replicas
 * @param failedReplicas the failed replicas
 * @param createdAt the created at
 * @param updatedAt the updated at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactReplicationJob(

        String replicationId,

        String artifactId,

        String sourceRuntimeId,

        int desiredReplicas,

        KnowledgeEvidenceArtifactReplicationState state,

        int successfulReplicas,

        int failedReplicas,

        Instant createdAt,

        Instant updatedAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceArtifactReplicationJob {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
