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
 * Represents a knowledge evidence artifact replica advertisement.
 *
 * <p>Its components capture `runtime id`, `tenant id`, `artifact ids`, `issued at`, `expires at`, and other values.</p>
 *
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param artifactIds the artifact ids
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param advertisementFingerprint the advertisement fingerprint
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactReplicaAdvertisement(

        String runtimeId,

        String tenantId,

        List<String> artifactIds,

        Instant issuedAt,

        Instant expiresAt,

        String advertisementFingerprint,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceArtifactReplicaAdvertisement {

        artifactIds =
                artifactIds == null
                        ? List.of()
                        : List.copyOf(artifactIds);

        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }

    public boolean activeAt(
            Instant now
    ) {

        return expiresAt == null ||
                now.isBefore(expiresAt);
    }
}
