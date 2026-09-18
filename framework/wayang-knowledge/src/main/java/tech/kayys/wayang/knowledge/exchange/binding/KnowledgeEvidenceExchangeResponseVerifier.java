package tech.kayys.wayang.knowledge.exchange.binding;

import tech.kayys.wayang.knowledge.exchange.KnowledgeEvidenceExchangeResponse;
import tech.kayys.wayang.knowledge.exchange.session.KnowledgeEvidenceExchangeRequestBinding;

import java.time.Instant;

/**
 * Defines the contract for knowledge evidence exchange response verifier operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeResponseVerifier {

    KnowledgeEvidenceExchangeResponseVerificationResult verify(
            KnowledgeEvidenceExchangeRequestBinding requestBinding,
            KnowledgeEvidenceExchangeResponseBinding responseBinding,
            KnowledgeEvidenceExchangeResponse response,
            String expectedRemoteRuntimeId,
            Instant now
    );
}
