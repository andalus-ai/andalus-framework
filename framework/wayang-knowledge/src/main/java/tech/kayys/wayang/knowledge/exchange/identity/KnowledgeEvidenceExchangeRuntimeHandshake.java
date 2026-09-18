package tech.kayys.wayang.knowledge.exchange.identity;

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
import java.util.Objects;

/**
 * Represents a knowledge evidence exchange runtime handshake.
 *
 * <p>Its components capture `handshake id`, `local runtime id`, `remote runtime id`, `local identity fingerprint`, `remote identity fingerprint`, and other values.</p>
 *
 * @param handshakeId the handshake id
 * @param localRuntimeId the local runtime id
 * @param remoteRuntimeId the remote runtime id
 * @param localIdentityFingerprint the local identity fingerprint
 * @param remoteIdentityFingerprint the remote identity fingerprint
 * @param localKeyId the local key id
 * @param localKeyVersion the local key version
 * @param remoteKeyId the remote key id
 * @param remoteKeyVersion the remote key version
 * @param localNonce the local nonce
 * @param remoteNonce the remote nonce
 * @param issuedAt the issued at
 * @param expiresAt the expires at
 * @param localAuthenticated the local authenticated
 * @param remoteAuthenticated the remote authenticated
 * @param mutuallyTrusted the mutually trusted
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeRuntimeHandshake(

        String handshakeId,

        String localRuntimeId,

        String remoteRuntimeId,

        String localIdentityFingerprint,

        String remoteIdentityFingerprint,

        String localKeyId,

        String localKeyVersion,

        String remoteKeyId,

        String remoteKeyVersion,

        String localNonce,

        String remoteNonce,

        Instant issuedAt,

        Instant expiresAt,

        boolean localAuthenticated,

        boolean remoteAuthenticated,

        boolean mutuallyTrusted,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeRuntimeHandshake {
        Objects.requireNonNull(handshakeId);
        Objects.requireNonNull(localRuntimeId);
        Objects.requireNonNull(remoteRuntimeId);

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean activeAt(Instant at) {

        return mutuallyTrusted &&
                (expiresAt == null ||
                        at.isBefore(expiresAt));
    }
}
