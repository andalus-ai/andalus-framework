package tech.kayys.wayang.knowledge.exchange.envelope;

/**
 * Represents a knowledge evidence exchange signed message.
 *
 * <p>Its components capture `payload`, `envelope`.</p>
 *
 * @param payload the payload
 * @param envelope the envelope
 */


public record KnowledgeEvidenceExchangeSignedMessage<T>(
        T payload,
        KnowledgeEvidenceExchangeSignedEnvelope envelope
) {
}
