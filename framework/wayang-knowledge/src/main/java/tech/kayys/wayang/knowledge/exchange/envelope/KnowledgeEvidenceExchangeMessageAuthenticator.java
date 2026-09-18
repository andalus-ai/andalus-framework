package tech.kayys.wayang.knowledge.exchange.envelope;

/**
 * Defines the contract for knowledge evidence exchange message authenticator operations in the Wayang framework.
 */


public interface KnowledgeEvidenceExchangeMessageAuthenticator {

    KnowledgeEvidenceExchangeMessageAuthenticationAlgorithm algorithm();

    String keyId();

    String keyVersion();

    byte[] authenticate(byte[] message);

    boolean verify(
            byte[] message,
            byte[] authentication
    );
}
