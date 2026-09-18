package tech.kayys.wayang.knowledge.governance;

import java.util.Set;

/**
 * Represents a governance rule.
 *
 * <p>Its components capture `id`, `classification`, `allowed scopes`, `min trust level`.</p>
 *
 * @param id the id
 * @param classification the classification
 * @param allowedScopes the allowed scopes
 * @param minTrustLevel the min trust level
 */


public record GovernanceRule(
        String id,
        KnowledgeClassification classification,
        Set<String> allowedScopes,
        KnowledgeTrustLevel minTrustLevel
) {

    public GovernanceRule {
        allowedScopes = allowedScopes == null ? Set.of() : Set.copyOf(allowedScopes);
        classification = classification == null ? KnowledgeClassification.INTERNAL : classification;
        minTrustLevel = minTrustLevel == null ? KnowledgeTrustLevel.REVIEWED : minTrustLevel;
    }
}
