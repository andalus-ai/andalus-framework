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
 * Represents a knowledge evidence artifact replica.
 *
 * <p>Its components capture `artifact id`, `runtime id`, `tenant id`, `state`, `size`, and other values.</p>
 *
 * @param artifactId the artifact id
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param state the state
 * @param size the size
 * @param fingerprint the fingerprint
 * @param merkleRoot the merkle root
 * @param discoveredAt the discovered at
 * @param verifiedAt the verified at
 * @param lastSeenAt the last seen at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactReplica(

        String artifactId,

        String runtimeId,

        String tenantId,

        KnowledgeEvidenceArtifactReplicaState state,

        long size,

        String fingerprint,

        String merkleRoot,

        Instant discoveredAt,

        Instant verifiedAt,

        Instant lastSeenAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceArtifactReplica {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean available() {
        return state ==
                KnowledgeEvidenceArtifactReplicaState.AVAILABLE;
    }

    public boolean verified() {
        return state ==
                KnowledgeEvidenceArtifactReplicaState.VERIFIED
                || available();
    }
}
