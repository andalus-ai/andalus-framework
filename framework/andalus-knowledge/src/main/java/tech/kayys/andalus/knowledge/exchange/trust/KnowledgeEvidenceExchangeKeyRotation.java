package tech.kayys.andalus.knowledge.exchange.trust;

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
