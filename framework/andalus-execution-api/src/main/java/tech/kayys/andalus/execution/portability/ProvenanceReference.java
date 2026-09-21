package tech.kayys.andalus.execution.portability;

import java.util.Objects;

/**
 * Content-addressed pointer to temporal provenance data for portable executions.
 */
public record ProvenanceReference(String graphId, String merkleRoot, long eventCount) {

    public ProvenanceReference {
        Objects.requireNonNull(graphId, "graphId cannot be null");
        Objects.requireNonNull(merkleRoot, "merkleRoot cannot be null");
    }

    public static ProvenanceReference of(String graphId, String merkleRoot, long eventCount) {
        return new ProvenanceReference(graphId, merkleRoot, eventCount);
    }
}
