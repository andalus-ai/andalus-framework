package tech.kayys.wayang.knowledge.exchange.auth;

import tech.kayys.wayang.knowledge.exchange.KnowledgeEvidenceExchangeOperation;

import java.util.Map;
import java.util.Set;

/**
 * Represents a knowledge evidence capability negotiation result.
 *
 * <p>Its components capture `compatible`, `operations`, `hash algorithms`, `seal algorithms`, `streaming`, and other values.</p>
 *
 * @param compatible the compatible
 * @param operations the operations
 * @param hashAlgorithms the hash algorithms
 * @param sealAlgorithms the seal algorithms
 * @param streaming the streaming
 * @param partialVerification the partial verification
 * @param missingCapabilities the missing capabilities
 * @param metadata the metadata
 */


public record KnowledgeEvidenceCapabilityNegotiationResult(
        boolean compatible,
        Set<KnowledgeEvidenceExchangeOperation> operations,
        Set<String> hashAlgorithms,
        Set<String> sealAlgorithms,
        boolean streaming,
        boolean partialVerification,
        Set<String> missingCapabilities,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceCapabilityNegotiationResult {
        operations = operations == null ? Set.of() : Set.copyOf(operations);
        hashAlgorithms = hashAlgorithms == null ? Set.of() : Set.copyOf(hashAlgorithms);
        sealAlgorithms = sealAlgorithms == null ? Set.of() : Set.copyOf(sealAlgorithms);
        missingCapabilities = missingCapabilities == null ? Set.of() : Set.copyOf(missingCapabilities);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
