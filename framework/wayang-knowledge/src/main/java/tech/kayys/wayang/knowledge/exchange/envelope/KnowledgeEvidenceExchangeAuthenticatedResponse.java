package tech.kayys.wayang.knowledge.exchange.envelope;

import tech.kayys.wayang.knowledge.exchange.binding.KnowledgeEvidenceExchangeResponseEnvelope;

/**
 * Represents a knowledge evidence exchange authenticated response.
 *
 * <p>Its components capture `response`, `authentication`.</p>
 *
 * @param response the response
 * @param authentication the authentication
 */


public record KnowledgeEvidenceExchangeAuthenticatedResponse(
        KnowledgeEvidenceExchangeResponseEnvelope response,
        KnowledgeEvidenceExchangeSignedEnvelope authentication
) {
}
