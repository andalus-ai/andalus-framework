package tech.kayys.wayang.knowledge.exchange.uncertainty;

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
import tech.kayys.wayang.knowledge.exchange.coverage.*;
import tech.kayys.wayang.knowledge.exchange.gap.*;
import tech.kayys.wayang.knowledge.exchange.attribution.*;
import tech.kayys.wayang.knowledge.exchange.contradiction.*;
import tech.kayys.wayang.knowledge.exchange.factuality.*;
import tech.kayys.wayang.knowledge.exchange.uncertainty.*;
import tech.kayys.wayang.knowledge.exchange.compact.*;
import tech.kayys.wayang.knowledge.exchange.resolution.*;
import tech.kayys.wayang.knowledge.exchange.quorum.*;
import tech.kayys.wayang.knowledge.exchange.selection.*;
import tech.kayys.wayang.knowledge.exchange.coordination.*;
import tech.kayys.wayang.knowledge.exchange.attestation.*;
import tech.kayys.wayang.knowledge.exchange.proof.*;
import tech.kayys.wayang.knowledge.exchange.validity.*;
import tech.kayys.wayang.knowledge.exchange.lease.*;
import tech.kayys.wayang.knowledge.exchange.recovery.*;


import java.time.Instant;
import java.util.Map;
import java.util.Set;

/**
 * Represents a knowledge answer artifact index entry.
 *
 * <p>Its components capture `artifact id`, `response id`, `snapshot id`, `runtime id`, `tenant id`, and other values.</p>
 *
 * @param artifactId the artifact id
 * @param responseId the response id
 * @param snapshotId the snapshot id
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param agentId the agent id
 * @param status the status
 * @param disposition the disposition
 * @param confidence the confidence
 * @param verified the verified
 * @param sealed the sealed
 * @param createdAt the created at
 * @param effectiveAt the effective at
 * @param tags the tags
 * @param concepts the concepts
 * @param claimTypes the claim types
 * @param responseFingerprint the response fingerprint
 * @param metadata the metadata
 */


public record KnowledgeAnswerArtifactIndexEntry(

        String artifactId,

        String responseId,

        String snapshotId,

        String runtimeId,

        String tenantId,

        String workspaceId,

        String projectId,

        String agentId,

        String status,

        String disposition,

        double confidence,

        boolean verified,

        boolean sealed,

        Instant createdAt,

        Instant effectiveAt,

        Set<String> tags,

        Set<String> concepts,

        Set<String> claimTypes,

        String responseFingerprint,

        Map<String, String> metadata
) {

    public KnowledgeAnswerArtifactIndexEntry {
        tags =
                tags == null
                        ? Set.of()
                        : Set.copyOf(tags);

        concepts =
                concepts == null
                        ? Set.of()
                        : Set.copyOf(concepts);

        claimTypes =
                claimTypes == null
                        ? Set.of()
                        : Set.copyOf(claimTypes);

        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }
}
