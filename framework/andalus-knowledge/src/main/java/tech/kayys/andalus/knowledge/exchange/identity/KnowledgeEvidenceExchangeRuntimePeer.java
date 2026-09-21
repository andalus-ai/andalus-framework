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
 * Represents a knowledge evidence exchange runtime peer.
 *
 * <p>Its components capture `local runtime id`, `remote runtime id`, `remote identity fingerprint`, `trust anchor id`, `trusted from`, and other values.</p>
 *
 * @param localRuntimeId the local runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param remoteIdentityFingerprint the remote identity fingerprint
 * @param trustAnchorId the trust anchor id
 * @param trustedFrom the trusted from
 * @param trustedUntil the trusted until
 * @param trusted the trusted
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeRuntimePeer(

        String localRuntimeId,

        String remoteRuntimeId,

        String remoteIdentityFingerprint,

        String trustAnchorId,

        Instant trustedFrom,

        Instant trustedUntil,

        boolean trusted,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeRuntimePeer {
        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean trustedAt(Instant at) {

        if (!trusted) {
            return false;
        }

        if (trustedFrom != null &&
                at.isBefore(trustedFrom)) {
            return false;
        }

        return trustedUntil == null ||
                at.isBefore(trustedUntil);
    }
}
