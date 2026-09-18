package tech.kayys.wayang.knowledge.exchange.federation;

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
