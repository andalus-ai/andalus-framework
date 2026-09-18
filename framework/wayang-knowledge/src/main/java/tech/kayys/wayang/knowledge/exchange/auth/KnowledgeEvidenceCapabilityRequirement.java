package tech.kayys.wayang.knowledge.exchange.auth;

import tech.kayys.wayang.knowledge.exchange.KnowledgeEvidenceExchangeOperation;

import java.util.Map;
import java.util.Set;

/**
 * Represents a knowledge evidence capability requirement.
 *
 * <p>Its components capture `operations`, `hash algorithms`, `seal algorithms`, `streaming`, `partial verification`, and other values.</p>
 *
 * @param operations the operations
 * @param hashAlgorithms the hash algorithms
 * @param sealAlgorithms the seal algorithms
 * @param streaming the streaming
 * @param partialVerification the partial verification
 * @param metadata the metadata
 */


public record KnowledgeEvidenceCapabilityRequirement(
        Set<KnowledgeEvidenceExchangeOperation> operations,
        Set<String> hashAlgorithms,
        Set<String> sealAlgorithms,
        boolean streaming,
        boolean partialVerification,
        Map<String, String> metadata
) {
    public KnowledgeEvidenceCapabilityRequirement {
        operations = operations == null ? Set.of() : Set.copyOf(operations);
        hashAlgorithms = hashAlgorithms == null ? Set.of() : Set.copyOf(hashAlgorithms);
        sealAlgorithms = sealAlgorithms == null ? Set.of() : Set.copyOf(sealAlgorithms);
        metadata = metadata == null ? Map.of() : Map.copyOf(metadata);
    }
}
