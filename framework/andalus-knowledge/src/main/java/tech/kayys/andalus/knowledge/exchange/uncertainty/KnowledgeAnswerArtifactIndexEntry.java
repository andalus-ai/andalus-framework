package tech.kayys.andalus.knowledge.exchange.uncertainty;

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
import tech.kayys.andalus.knowledge.exchange.coverage.*;
import tech.kayys.andalus.knowledge.exchange.gap.*;
import tech.kayys.andalus.knowledge.exchange.attribution.*;
import tech.kayys.andalus.knowledge.exchange.contradiction.*;
import tech.kayys.andalus.knowledge.exchange.factuality.*;
import tech.kayys.andalus.knowledge.exchange.uncertainty.*;
import tech.kayys.andalus.knowledge.exchange.compact.*;
import tech.kayys.andalus.knowledge.exchange.resolution.*;
import tech.kayys.andalus.knowledge.exchange.quorum.*;
import tech.kayys.andalus.knowledge.exchange.selection.*;
import tech.kayys.andalus.knowledge.exchange.coordination.*;
import tech.kayys.andalus.knowledge.exchange.attestation.*;
import tech.kayys.andalus.knowledge.exchange.proof.*;
import tech.kayys.andalus.knowledge.exchange.validity.*;
import tech.kayys.andalus.knowledge.exchange.lease.*;
import tech.kayys.andalus.knowledge.exchange.recovery.*;


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
