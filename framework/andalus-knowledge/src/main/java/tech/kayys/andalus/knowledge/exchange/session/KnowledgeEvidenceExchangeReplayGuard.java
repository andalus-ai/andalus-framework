package tech.kayys.andalus.knowledge.exchange.session;

import java.time.Instant;

/**
 * Defines the contract for knowledge evidence exchange replay guard operations in the Andalus framework.
 */


public interface KnowledgeEvidenceExchangeReplayGuard {

    KnowledgeEvidenceExchangeReplayStatus checkAndRecord(
            KnowledgeEvidenceExchangeRequestBinding binding,
            String principalId,
            Instant now
    );

    void removeExpired(Instant now);
}
