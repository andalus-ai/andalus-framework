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
 * Represents a knowledge evidence artifact inventory entry.
 *
 * <p>Its components capture `artifact id`, `size`, `fingerprint`, `merkle root`, `revoked`, and other values.</p>
 *
 * @param artifactId the artifact id
 * @param size the size
 * @param fingerprint the fingerprint
 * @param merkleRoot the merkle root
 * @param revoked the revoked
 * @param createdAt the created at
 * @param lastModifiedAt the last modified at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactInventoryEntry(

        String artifactId,

        long size,

        String fingerprint,

        String merkleRoot,

        boolean revoked,

        Instant createdAt,

        Instant lastModifiedAt,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceArtifactInventoryEntry {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
