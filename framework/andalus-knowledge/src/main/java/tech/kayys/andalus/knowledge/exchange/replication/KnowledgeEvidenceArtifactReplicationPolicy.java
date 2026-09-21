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


import java.util.Map;

/**
 * Represents a knowledge evidence artifact replication policy.
 *
 * <p>Its components capture `minimum replicas`, `target replicas`, `require verification`, `allow remote replication`, `allow offline queue`, and other values.</p>
 *
 * @param minimumReplicas the minimum replicas
 * @param targetReplicas the target replicas
 * @param requireVerification the require verification
 * @param allowRemoteReplication the allow remote replication
 * @param allowOfflineQueue the allow offline queue
 * @param maxArtifactBytes the max artifact bytes
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactReplicationPolicy(

        int minimumReplicas,

        int targetReplicas,

        boolean requireVerification,

        boolean allowRemoteReplication,

        boolean allowOfflineQueue,

        long maxArtifactBytes,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceArtifactReplicationPolicy {

        if (minimumReplicas < 1) {
            throw new IllegalArgumentException(
                    "minimumReplicas must be >= 1"
            );
        }

        if (targetReplicas < minimumReplicas) {
            throw new IllegalArgumentException(
                    "targetReplicas must be >= minimumReplicas"
            );
        }

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public static KnowledgeEvidenceArtifactReplicationPolicy defaults() {

        return new KnowledgeEvidenceArtifactReplicationPolicy(
                1,
                2,
                true,
                true,
                true,
                4L * 1024L * 1024L * 1024L,
                Map.of()
        );
    }
}
