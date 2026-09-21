package tech.kayys.andalus.knowledge.exchange.sync;

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
 * Represents a knowledge evidence artifact revocation tombstone.
 *
 * <p>Its components capture `artifact id`, `tombstone id`, `tenant id`, `reason`, `revoked by`, and other values.</p>
 *
 * @param artifactId the artifact id
 * @param tombstoneId the tombstone id
 * @param tenantId the tenant id
 * @param reason the reason
 * @param revokedBy the revoked by
 * @param revokedAt the revoked at
 * @param expiresAt the expires at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactRevocationTombstone(

        String artifactId,

        String tombstoneId,

        String tenantId,

        String reason,

        String revokedBy,

        Instant revokedAt,

        Instant expiresAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceArtifactRevocationTombstone {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant now) {

        return expiresAt == null ||
                now.isBefore(expiresAt);
    }
}
