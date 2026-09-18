package tech.kayys.wayang.knowledge.exchange.trust;

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


import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence exchange key lifecycle event.
 *
 * <p>Its components capture `event id`, `key id`, `key version`, `runtime id`, `tenant id`, and other values.</p>
 *
 * @param eventId the event id
 * @param keyId the key id
 * @param keyVersion the key version
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param type the type
 * @param actorId the actor id
 * @param reason the reason
 * @param createdAt the created at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeKeyLifecycleEvent(

        String eventId,

        String keyId,

        String keyVersion,

        String runtimeId,

        String tenantId,

        Type type,

        String actorId,

        String reason,

        Instant createdAt,

        Map<String, String> metadata

) {
    /**
     * Enumerates the type values used by the Wayang framework.
     */


    public enum Type {
        REGISTERED,
        ACTIVATED,
        ROTATED,
        REVOKED,
        EXPIRED,
        TRUST_GRANTED,
        TRUST_REMOVED
    }

    public KnowledgeEvidenceExchangeKeyLifecycleEvent {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
