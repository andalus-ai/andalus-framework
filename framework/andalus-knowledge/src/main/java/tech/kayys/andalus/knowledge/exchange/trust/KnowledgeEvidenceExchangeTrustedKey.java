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
import java.util.Map;
import java.util.Objects;

/**
 * Represents a knowledge evidence exchange trusted key.
 *
 * <p>Its components capture `key id`, `key version`, `algorithm`, `runtime id`, `tenant id`, and other values.</p>
 *
 * @param keyId the key id
 * @param keyVersion the key version
 * @param algorithm the algorithm
 * @param runtimeId the runtime id
 * @param tenantId the tenant id
 * @param validFrom the valid from
 * @param validUntil the valid until
 * @param trusted the trusted
 * @param revoked the revoked
 * @param trustAnchorId the trust anchor id
 * @param metadata the metadata
 */


public record KnowledgeEvidenceExchangeTrustedKey(

        String keyId,

        String keyVersion,

        KnowledgeEvidenceExchangeMessageAuthenticationAlgorithm algorithm,

        String runtimeId,

        String tenantId,

        Instant validFrom,

        Instant validUntil,

        boolean trusted,

        boolean revoked,

        String trustAnchorId,

        Map<String, String> metadata

) {

    public KnowledgeEvidenceExchangeTrustedKey {
        Objects.requireNonNull(keyId, "keyId");
        Objects.requireNonNull(keyVersion, "keyVersion");
        Objects.requireNonNull(algorithm, "algorithm");

        metadata = metadata == null
                ? Map.of()
                : Map.copyOf(metadata);
    }

    public boolean isValidAt(Instant instant) {

        Objects.requireNonNull(instant, "instant");

        if (revoked) {
            return false;
        }

        if (!trusted) {
            return false;
        }

        if (validFrom != null && instant.isBefore(validFrom)) {
            return false;
        }

        if (validUntil != null && instant.isAfter(validUntil)) {
            return false;
        }

        return true;
    }

    public KnowledgeEvidenceExchangeKeyTrustStatus trustStatus(
            Instant instant
    ) {

        if (revoked) {
            return KnowledgeEvidenceExchangeKeyTrustStatus.REVOKED;
        }

        if (!trusted) {
            return KnowledgeEvidenceExchangeKeyTrustStatus.UNTRUSTED;
        }

        if (validFrom != null && instant.isBefore(validFrom)) {
            return KnowledgeEvidenceExchangeKeyTrustStatus.NOT_YET_VALID;
        }

        if (validUntil != null && instant.isAfter(validUntil)) {
            return KnowledgeEvidenceExchangeKeyTrustStatus.EXPIRED;
        }

        return KnowledgeEvidenceExchangeKeyTrustStatus.TRUSTED;
    }
}
