package tech.kayys.wayang.knowledge.exchange.auth;

import tech.kayys.wayang.knowledge.exchange.KnowledgeEvidenceExchangeRequest;

import java.time.Instant;
import java.util.Map;

/**
 * Represents a knowledge evidence exchange authorization context.
 *
 * <p>Its components capture `request`, `principal`, `timestamp`, `attributes`.</p>
 *
 * @param request the request
 * @param principal the principal
 * @param timestamp the timestamp
 * @param attributes the attributes
 */


public record KnowledgeEvidenceExchangeAuthorizationContext(
        KnowledgeEvidenceExchangeRequest request,
        KnowledgeEvidenceExchangePrincipal principal,
        Instant timestamp,
        Map<String, String> attributes
) {
    public KnowledgeEvidenceExchangeAuthorizationContext {
        attributes = attributes == null ? Map.of() : Map.copyOf(attributes);
    }
}
