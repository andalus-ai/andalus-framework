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
 * Represents a knowledge evidence artifact replica location.
 *
 * <p>Its components capture `runtime id`, `endpoint id`, `tenant id`, `region`, `local`, and other values.</p>
 *
 * @param runtimeId the runtime id
 * @param endpointId the endpoint id
 * @param tenantId the tenant id
 * @param region the region
 * @param local the local
 * @param online the online
 * @param metadata the metadata
 */


public record KnowledgeEvidenceArtifactReplicaLocation(

        String runtimeId,

        String endpointId,

        String tenantId,

        String region,

        boolean local,

        boolean online,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceArtifactReplicaLocation {

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
