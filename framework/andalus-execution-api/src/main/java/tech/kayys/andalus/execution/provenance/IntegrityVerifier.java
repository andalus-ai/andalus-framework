package tech.kayys.andalus.execution.provenance;

/**
 * Verifies cryptographic and chronological integrity of a provenance chain.
 */
public interface IntegrityVerifier {

    boolean verifyChain(ProvenanceChain chain);
}
