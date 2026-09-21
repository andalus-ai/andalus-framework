package tech.kayys.andalus.knowledge.exchange.federation;

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
 * Represents a knowledge evidence runtime location.
 *
 * <p>Its components capture `runtime id`, `tenant id`, `workspace id`, `project id`, `local`, and other values.</p>
 *
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param workspaceId the workspace id
 * @param projectId the project id
 * @param local the local
 * @param online the online
 * @param trusted the trusted
 * @param verified the verified
 * @param lastSeenAt the last seen at
 * @param estimatedLatencyMs the estimated latency ms
 * @param availableBytes the available bytes
 * @param metadata the metadata
 */


public record KnowledgeEvidenceRuntimeLocation(

        String runtimeId,

        String tenantId,

        String workspaceId,

        String projectId,

        boolean local,

        boolean online,

        boolean trusted,

        boolean verified,

        Instant lastSeenAt,

        int estimatedLatencyMs,

        long availableBytes,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceRuntimeLocation {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
