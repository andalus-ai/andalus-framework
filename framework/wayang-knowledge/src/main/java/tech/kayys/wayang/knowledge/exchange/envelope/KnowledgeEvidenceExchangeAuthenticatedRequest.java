package tech.kayys.wayang.knowledge.exchange.envelope;

import tech.kayys.wayang.knowledge.exchange.KnowledgeEvidenceExchangeRequest;
import tech.kayys.wayang.knowledge.exchange.session.KnowledgeEvidenceExchangeRequestBinding;

/**
 * Represents a knowledge evidence exchange authenticated request.
 *
 * <p>Its components capture `request`, `binding`, `authentication`.</p>
 *
 * @param request the request
 * @param binding the binding
 * @param authentication the authentication
 */


public record KnowledgeEvidenceExchangeAuthenticatedRequest(
        KnowledgeEvidenceExchangeRequest request,
        KnowledgeEvidenceExchangeRequestBinding binding,
        KnowledgeEvidenceExchangeSignedEnvelope authentication
) {
}
