package tech.kayys.wayang.execution.provenance;

/**
 * Verifies cryptographic and chronological integrity of a provenance chain.
 */
public interface IntegrityVerifier {

    boolean verifyChain(ProvenanceChain chain);
}
