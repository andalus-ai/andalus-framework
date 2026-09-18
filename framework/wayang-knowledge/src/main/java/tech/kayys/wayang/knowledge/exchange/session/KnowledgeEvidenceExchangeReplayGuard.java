package tech.kayys.wayang.knowledge.exchange.session;

import java.time.Instant;

/**
 * Defines the contract for knowledge evidence exchange replay guard operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeReplayGuard {

    KnowledgeEvidenceExchangeReplayStatus checkAndRecord(
            KnowledgeEvidenceExchangeRequestBinding binding,
            String principalId,
            Instant now
    );

    void removeExpired(Instant now);
}
