package tech.kayys.wayang.knowledge.exchange.envelope;

import java.time.Instant;
import java.util.Optional;

/**
 * Defines the contract for knowledge evidence exchange key resolver operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeKeyResolver {

    Optional<KnowledgeEvidenceExchangeMessageAuthenticator> resolveSigner(
            String keyId,
            String keyVersion,
            Instant now
    );

    Optional<KnowledgeEvidenceExchangeMessageAuthenticator> resolveVerifier(
            String keyId,
            String keyVersion,
            Instant now
    );
}
