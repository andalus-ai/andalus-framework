package tech.kayys.wayang.knowledge.exchange.sync;

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
