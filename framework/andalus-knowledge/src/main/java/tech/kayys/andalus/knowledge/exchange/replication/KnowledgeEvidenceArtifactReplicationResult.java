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


import java.util.List;
import java.util.Map;

/**
 * Represents a knowledge evidence artifact replication result.
 *
 * <p>Its components capture `replication id`, `artifact id`, `requested replicas`, `successful replicas`, `verified replicas`, and other values.</p>
 *
 * @param replicationId the replication id
 * @param artifactId the artifact id
 * @param requestedReplicas the requested replicas
 * @param successfulReplicas the successful replicas
 * @param verifiedReplicas the verified replicas
 * @param successfulRuntimeIds the successful runtime ids
 * @param failedRuntimeIds the failed runtime ids
 * @param quorumSatisfied the quorum satisfied
 * @param diagnostics the diagnostics
 */


public record KnowledgeEvidenceArtifactReplicationResult(

        String replicationId,

        String artifactId,

        int requestedReplicas,

        int successfulReplicas,

        int verifiedReplicas,

        List<String> successfulRuntimeIds,

        List<String> failedRuntimeIds,

        boolean quorumSatisfied,

        Map<String, String> diagnostics

) {

    public KnowledgeEvidenceArtifactReplicationResult {

        successfulRuntimeIds =
                successfulRuntimeIds == null
                        ? List.of()
                        : List.copyOf(successfulRuntimeIds);

        failedRuntimeIds =
                failedRuntimeIds == null
                        ? List.of()
                        : List.copyOf(failedRuntimeIds);

        diagnostics =
                diagnostics == null
                        ? Map.of()
                        : Map.copyOf(diagnostics);
    }
}
