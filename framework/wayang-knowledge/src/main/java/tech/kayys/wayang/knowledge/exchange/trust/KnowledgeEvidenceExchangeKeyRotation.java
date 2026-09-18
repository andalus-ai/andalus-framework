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
import java.util.Objects;

/**
 * Represents a knowledge evidence exchange key rotation.
 *
 * <p>Its components capture `key id`, `previous version`, `new version`, `activated at`, `previous valid until`, and other values.</p>
 *
 * @param keyId the key id
 * @param previousVersion the previous version
 * @param newVersion the new version
 * @param activatedAt the activated at
 * @param previousValidUntil the previous valid until
 * @param runtimeId the runtime id
 * @param reason the reason
 */


public record KnowledgeEvidenceExchangeKeyRotation(

        String keyId,

        String previousVersion,

        String newVersion,

        Instant activatedAt,

        Instant previousValidUntil,

        String runtimeId,

        String reason

) {

    public KnowledgeEvidenceExchangeKeyRotation {
        Objects.requireNonNull(keyId, "keyId");
        Objects.requireNonNull(newVersion, "newVersion");
        Objects.requireNonNull(activatedAt, "activatedAt");
    }
}
