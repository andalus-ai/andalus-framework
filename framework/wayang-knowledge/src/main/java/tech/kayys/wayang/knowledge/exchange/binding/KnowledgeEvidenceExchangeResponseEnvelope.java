package tech.kayys.wayang.knowledge.exchange.binding;

import tech.kayys.wayang.knowledge.exchange.KnowledgeEvidenceExchangeResponse;

/**
 * Represents a knowledge evidence exchange response envelope.
 *
 * <p>Its components capture `response`, `binding`.</p>
 *
 * @param response the response
 * @param binding the binding
 */


public record KnowledgeEvidenceExchangeResponseEnvelope(
        KnowledgeEvidenceExchangeResponse response,
        KnowledgeEvidenceExchangeResponseBinding binding
) {
}
