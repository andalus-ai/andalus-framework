package tech.kayys.andalus.knowledge.exchange.identity;

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


import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence exchange runtime security event.
 *
 * <p>Its components capture `event id`, `local runtime id`, `remote runtime id`, `type`, `successful`, and other values.</p>
 *
 * @param eventId the event id
 * @param localRuntimeId the local runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param type the type
 * @param successful the successful
 * @param reason the reason
 * @param createdAt the created at
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeRuntimeSecurityEvent(

        String eventId,

        String localRuntimeId,

        String remoteRuntimeId,

        Type type,

        boolean successful,

        String reason,

        Instant createdAt,

        Map<String, String> metadata

) {
    /**
     * Enumerates the type values used by the Wayang framework.
     */


    public enum Type {

        ENROLLMENT_REQUESTED,

        ENROLLMENT_APPROVED,

        ENROLLMENT_DENIED,

        HANDSHAKE_STARTED,

        HANDSHAKE_COMPLETED,

        HANDSHAKE_DENIED,

        PEER_TRUSTED,

        PEER_REVOKED
    }

    public KnowledgeEvidenceExchangeRuntimeSecurityEvent {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }
}
