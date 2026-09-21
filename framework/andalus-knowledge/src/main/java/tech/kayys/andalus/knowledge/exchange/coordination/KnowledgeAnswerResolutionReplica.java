package tech.kayys.andalus.knowledge.exchange.coordination;

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

/**
 * Represents a knowledge answer resolution replica.
 *
 * <p>Its components capture `key fingerprint`, `runtime id`, `tenant id`, `state`, `resolution fingerprint`, and other values.</p>
 *
 * @param keyFingerprint the key fingerprint
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param state the state
 * @param resolutionFingerprint the resolution fingerprint
 * @param dependencyFingerprint the dependency fingerprint
 * @param createdAt the created at
 * @param verifiedAt the verified at
 * @param lastSeenAt the last seen at
 * @param metadata the metadata
 */


public record KnowledgeAnswerResolutionReplica(

        String keyFingerprint,

        String runtimeId,

        String tenantId,

        KnowledgeAnswerResolutionReplicationState state,

        String resolutionFingerprint,

        String dependencyFingerprint,

        Instant createdAt,

        Instant verifiedAt,

        Instant lastSeenAt,

        Map<String, String> metadata
) {

    public KnowledgeAnswerResolutionReplica {

        metadata =
                metadata == null
                        ? Map.of()
                        : Map.copyOf(metadata);
    }

    public boolean available() {
        return state
                == KnowledgeAnswerResolutionReplicationState
                .AVAILABLE;
    }
}
